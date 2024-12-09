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
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.ppxb.latte.starter.core.constant.StringConstants;
import com.ppxb.latte.starter.core.exception.BaseException;
import com.ppxb.latte.starter.security.crypto.annotation.FieldEncrypt;
import com.ppxb.latte.starter.security.crypto.encryptor.IEncryptor;
import com.ppxb.latte.starter.security.crypto.enums.Algorithm;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.MappedStatement;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public abstract class AbstractMyBatisInterceptor {

    private static final Map<Class<?>, List<Field>> CLASS_FIELD_CACHE = new ConcurrentHashMap<>();

    private static final Map<String, Map<String, FieldEncrypt>> ENCRYPT_PARAM_CACHE = new ConcurrentHashMap<>();

    protected List<Field> getEncryptFields(Object obj) {
        return obj == null ? Collections.emptyList() : getEncryptFields(obj.getClass());
    }

    protected List<Field> getEncryptFields(Class<?> clazz) {
        return CLASS_FIELD_CACHE.computeIfAbsent(clazz, k -> Arrays.stream(ReflectUtil.getFields(clazz))
            .filter(field -> String.class.equals(field.getType()))
            .filter(field -> null != field.getAnnotation(FieldEncrypt.class))
            .toList());
    }

    protected IEncryptor getEncryptor(FieldEncrypt fieldEncrypt) {
        Class<? extends IEncryptor> encryptorClass = fieldEncrypt.encryptor();
        if (encryptorClass == IEncryptor.class) {
            Algorithm algorithm = fieldEncrypt.value();
            return ReflectUtil.newInstance(algorithm.getEncryptor());
        }
        return SpringUtil.getBean(encryptorClass);
    }

    protected Map<String, FieldEncrypt> getEncryptParameters(MappedStatement mappedStatement) {
        String mappedStatementId = mappedStatement.getId();
        return ENCRYPT_PARAM_CACHE.computeIfAbsent(mappedStatementId, key -> {
            Method method = getMethod(mappedStatementId);
            if (null == method) {
                return Collections.emptyMap();
            }
            Map<String, FieldEncrypt> encryptMap = new HashMap<>();
            Parameter[] parameters = method.getParameters();

            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                FieldEncrypt fieldEncrypt = parameter.getAnnotation(FieldEncrypt.class);
                if (null == fieldEncrypt) {
                    continue;
                }

                String parameterName = getParameterName(parameter);
                encryptMap.put(parameterName, fieldEncrypt);

                if (String.class.equals(parameter.getType())) {
                    encryptMap.put("param" + (i + 1), fieldEncrypt);
                }
            }
            return encryptMap;
        });
    }

    private Method getMethod(String mappedStatementId) {
        try {
            String className = CharSequenceUtil.subBefore(mappedStatementId, StringConstants.DOT, true);
            String methodName = CharSequenceUtil.subAfter(mappedStatementId, StringConstants.DOT, true);
            Method[] methods = ReflectUtil.getMethods(Class.forName(className));
            return Stream.of(methods).filter(method -> method.getName().equals(methodName)).findFirst().orElse(null);
        } catch (ClassNotFoundException e) {
            throw new BaseException(e);
        }
    }

    private String getParameterName(Parameter parameter) {
        Param param = parameter.getAnnotation(Param.class);
        return null != param ? param.value() : parameter.getName();
    }
}
