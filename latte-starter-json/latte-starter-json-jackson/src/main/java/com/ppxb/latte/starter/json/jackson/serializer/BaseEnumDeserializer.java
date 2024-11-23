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



package com.ppxb.latte.starter.json.jackson.serializer;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.ppxb.latte.starter.core.enums.BaseEnum;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

@JacksonStdImpl
public class BaseEnumDeserializer extends JsonDeserializer<BaseEnum<?>> {

    public static final BaseEnumDeserializer SERIALIZER_INSTANCE = new BaseEnumDeserializer();

    @Override
    public BaseEnum<?> deserialize(JsonParser jsonParser,
                                   DeserializationContext deserializationContext) throws IOException {
        Class<?> targetClass = jsonParser.currentValue().getClass();
        String fieldName = jsonParser.currentName();
        String value = jsonParser.getText();
        return this.getEnum(targetClass, value, fieldName);
    }

    private BaseEnum<?> getEnum(Class<?> targetClass, String value, String fieldName) {
        Field field = ReflectUtil.getField(targetClass, fieldName);
        Class<?> fieldTypeClass = field.getType();
        Object[] enumConstants = fieldTypeClass.getEnumConstants();

        String targetValue = Convert.toStr(value);
        for (Object enumConstant : enumConstants) {
            if (ClassUtil.isAssignable(BaseEnum.class, fieldTypeClass)) {
                BaseEnum<?> baseEnum = (BaseEnum<?>)enumConstant;
                if (Objects.equals(Convert.toStr(baseEnum.getValue()), targetValue)) {
                    return baseEnum;
                }
            }
        }
        return null;
    }
}
