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


package com.ppxb.latte.starter.apidoc.handler;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ClassUtil;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.ppxb.latte.starter.apidoc.util.DocUtils;
import com.ppxb.latte.starter.core.enums.BaseEnum;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("all")
public class BaseEnumParameterHandler implements ParameterCustomizer, PropertyCustomizer {

    @Override
    public Parameter customize(Parameter parameterModel, MethodParameter methodParameter) {
        Class<?> parameterType = methodParameter.getParameterType();
        if (!ClassUtil.isAssignable(BaseEnum.class, parameterType)) {
            return parameterModel;
        }
        String description = parameterModel.getDescription();
        if (CharSequenceUtil.contains(description, "color:red")) {
            return parameterModel;
        }
        configureSchema(parameterModel.getSchema(), parameterType);
        parameterModel.setDescription(appendEnumDescription(description, parameterType));
        return parameterModel;
    }

    @Override
    public Schema customize(Schema property, AnnotatedType type) {
        Class<?> rawClass = resolveRawClass(type.getType());
        if (!ClassUtil.isAssignable(BaseEnum.class, rawClass)) {
            return property;
        }
        configureSchema(property, rawClass);
        property.setDescription(appendEnumDescription(property.getDescription(), rawClass));
        return property;
    }

    private void configureSchema(Schema schema, Class<?> enumClass) {
        BaseEnum[] enums = (BaseEnum[]) enumClass.getEnumConstants();
        List<String> valueList = Arrays.stream(enums)
                .map(e -> e.getValue().toString())
                .toList();
        String enumValueType = DocUtils.getEnumValueTypeAsString(enumClass);
        schema.setEnum(valueList);
        schema.setType(enumValueType);
        schema.setFormat(DocUtils.resolveFormat(enumValueType));
    }

    private String appendEnumDescription(String originalDescription, Class<?> enumClass) {
        return originalDescription + "<span style='color:red'>" + DocUtils.getDescMap(enumClass) + "</span>";
    }

    private Class<?> resolveRawClass(Type type) {
        return switch (type) {
            case SimpleType simpleType -> simpleType.getRawClass();
            case CollectionType collectionType -> collectionType.getContentType().getRawClass();
            default -> Object.class;
        };
    }
}
