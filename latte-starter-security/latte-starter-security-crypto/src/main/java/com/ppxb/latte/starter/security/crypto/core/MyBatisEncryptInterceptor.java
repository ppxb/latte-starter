/*
 * MIT License
 *
 * Copyright (c) 2024 ppxb
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */



package com.ppxb.latte.starter.security.crypto.core;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.ppxb.latte.starter.core.constant.StringConstants;
import com.ppxb.latte.starter.core.exception.BaseException;
import com.ppxb.latte.starter.security.crypto.annotation.FieldEncrypt;
import com.ppxb.latte.starter.security.crypto.autoconfigure.CryptoProperties;
import com.ppxb.latte.starter.security.crypto.encryptor.IEncryptor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyBatisEncryptInterceptor extends AbstractMyBatisInterceptor implements InnerInterceptor {

    private static final Pattern PARAM_PAIRS_PATTERN = Pattern
        .compile("#\\{ew\\.paramNameValuePairs\\.(" + Constants.WRAPPER_PARAM + "\\d+)}");

    private final CryptoProperties properties;

    public MyBatisEncryptInterceptor(CryptoProperties properties) {
        this.properties = properties;
    }

    @Override
    public void beforeQuery(Executor executor,
                            MappedStatement MappedStatement,
                            Object parameter,
                            RowBounds rowBounds,
                            ResultHandler resultHandler,
                            BoundSql boundSql) throws SQLException {
        if (null == parameter) {
            return;
        }
        if (parameter instanceof Map parameterMap) {
            encryptQueryParameter(parameterMap, MappedStatement);
        }
    }

    @Override
    public void beforeUpdate(Executor executor, MappedStatement mappedStatement, Object parameter) throws SQLException {
        if (null == parameter) {
            return;
        }
        if (parameter instanceof Map parameterMap) {
            encryptMap(parameterMap, mappedStatement);
        } else {
            encryptEntity(super.getEncryptFields(parameter), parameter);
        }
    }

    private void encryptMap(Map<String, Object> parameterMap, MappedStatement mappedStatement) {
        Object parameter;
        // 别名带有 et（针对 MP 的 updateById、update 等方法）
        if (parameterMap.containsKey(Constants.ENTITY) && null != (parameter = parameterMap.get(Constants.ENTITY))) {
            encryptEntity(super.getEncryptFields(parameter), parameter);
        }
        // 别名带有 ew（针对 MP 的 UpdateWrapper、LambdaUpdateWrapper 等参数）
        if (parameterMap.containsKey(Constants.WRAPPER) && null != (parameter = parameterMap.get(Constants.WRAPPER))) {
            encryptUpdateWrapper(parameter, mappedStatement);
        }
    }

    private void encryptQueryParameter(Map<String, Object> parameterMap, MappedStatement mappedStatement) {
        Map<String, FieldEncrypt> encryptParamterMap = super.getEncryptParameters(mappedStatement);
        for (Map.Entry<String, Object> parameterEntrySet : parameterMap.entrySet()) {
            String parameterName = parameterEntrySet.getKey();
            Object parameterValue = parameterEntrySet.getValue();
            if (null == parameterValue || ClassUtil.isBasicType(parameterValue
                .getClass()) || parameterValue instanceof AbstractWrapper<?, ?, ?>) {
                continue;
            }
            if (parameterValue instanceof String str) {
                FieldEncrypt fieldEncrypt = encryptParamterMap.get(parameterName);
                if (null != fieldEncrypt) {
                    parameterMap.put(parameterName, doEncrypt(str, fieldEncrypt));
                }
            } else {
                encryptEntity(super.getEncryptFields(parameterValue), parameterValue);
            }
        }
    }

    private void encryptUpdateWrapper(Object parameter, MappedStatement mappedStatement) {
        if (parameter instanceof AbstractWrapper<?, ?, ?> updateWrapper) {
            String sqlSet = updateWrapper.getSqlSet();
            if (CharSequenceUtil.isBlank(sqlSet)) {
                return;
            }
            // 将 name=#{ew.paramNameValuePairs.xxx},age=#{ew.paramNameValuePairs.xxx} 分离
            String[] elArr = sqlSet.split(StringConstants.COMMA);
            Map<String, String> propMap = new HashMap<>(elArr.length);
            Arrays.stream(elArr).forEach(el -> {
                String[] elPart = el.split(StringConstants.EQUALS);
                propMap.put(elPart[0], elPart[1]);
            });
            // 获取加密字段
            Class<?> entityClass = mappedStatement.getParameterMap().getType();
            List<Field> encryptFieldList = super.getEncryptFields(entityClass);
            for (Field field : encryptFieldList) {
                FieldEncrypt fieldEncrypt = field.getAnnotation(FieldEncrypt.class);
                String el = propMap.get(field.getName());
                if (CharSequenceUtil.isBlank(el)) {
                    continue;
                }
                Matcher matcher = PARAM_PAIRS_PATTERN.matcher(el);
                if (matcher.matches()) {
                    String valueKey = matcher.group(1);
                    Object value = updateWrapper.getParamNameValuePairs().get(valueKey);
                    Object ciphertext = this.doEncrypt(value, fieldEncrypt);
                    updateWrapper.getParamNameValuePairs().put(valueKey, ciphertext);
                }
            }
        }
    }

    private void encryptEntity(List<Field> fieldList, Object entity) {
        for (Field field : fieldList) {
            IEncryptor encryptor = super.getEncryptor(field.getAnnotation(FieldEncrypt.class));
            Object fieldValue = ReflectUtil.getFieldValue(entity, field);
            if (null == fieldValue) {
                continue;
            }
            String password = ObjectUtil.defaultIfBlank(field.getAnnotation(FieldEncrypt.class).password(), properties
                .getPassword());
            String ciphertext;
            try {
                ciphertext = encryptor.encrypt(fieldValue.toString(), password, properties.getPublicKey());
            } catch (Exception e) {
                throw new BaseException(e);
            }
            ReflectUtil.setFieldValue(entity, field, ciphertext);
        }
    }

    private Object doEncrypt(Object parameter, FieldEncrypt fieldEncrypt) {
        if (null == parameter) {
            return null;
        }
        IEncryptor encryptor = super.getEncryptor(fieldEncrypt);
        String password = ObjectUtil.defaultIfBlank(fieldEncrypt.password(), properties.getPassword());
        try {
            return encryptor.encrypt(parameter.toString(), password, properties.getPublicKey());
        } catch (Exception e) {
            throw new BaseException(e);
        }
    }
}
