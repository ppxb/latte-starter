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

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.ppxb.latte.starter.security.crypto.annotation.FieldEncrypt;
import com.ppxb.latte.starter.security.crypto.autoconfigure.CryptoProperties;
import com.ppxb.latte.starter.security.crypto.encryptor.IEncryptor;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.type.SimpleTypeRegistry;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.List;

@Intercepts({@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})})
public class MyBatisDecryptInterceptor extends AbstractMyBatisInterceptor implements Interceptor {

    private CryptoProperties properties;

    public MyBatisDecryptInterceptor() {
    }

    public MyBatisDecryptInterceptor(CryptoProperties properties) {
        this.properties = properties;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object obj = invocation.proceed();
        if (null == obj || !(invocation.getTarget() instanceof ResultSetHandler)) {
            return obj;
        }
        List<?> resultList = (List<?>)obj;
        for (Object result : resultList) {
            // String、Integer、Long 等简单类型对象无需处理
            if (SimpleTypeRegistry.isSimpleType(result.getClass())) {
                continue;
            }
            List<Field> fieldList = super.getEncryptFields(result);
            for (Field field : fieldList) {
                IEncryptor encryptor = super.getEncryptor(field.getAnnotation(FieldEncrypt.class));
                Object fieldValue = ReflectUtil.getFieldValue(result, field);
                if (null == fieldValue) {
                    continue;
                }
                String password = ObjectUtil.defaultIfBlank(field.getAnnotation(FieldEncrypt.class)
                    .password(), properties.getPassword());
                String ciphertext = encryptor.decrypt(fieldValue.toString(), password, properties.getPrivateKey());
                ReflectUtil.setFieldValue(result, field, ciphertext);
            }
        }
        return resultList;
    }
}
