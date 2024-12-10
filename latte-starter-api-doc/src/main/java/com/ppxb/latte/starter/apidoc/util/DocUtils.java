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



package com.ppxb.latte.starter.apidoc.util;

import com.ppxb.latte.starter.core.enums.BaseEnum;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DocUtils {

    private DocUtils() {
    }

    public static String getEnumValueTypeAsString(Class<?> enumClass) {
        Type[] interfaces = enumClass.getGenericInterfaces();
        Map<Class<?>, String> typeMap = Map
            .of(Integer.class, "integer", Long.class, "long", Double.class, "number", String.class, "string");

        for (Type type : interfaces) {
            if (type instanceof ParameterizedType parameterizedType && parameterizedType
                .getRawType() == BaseEnum.class) {
                Type actualType = parameterizedType.getActualTypeArguments()[0];
                if (actualType instanceof Class<?> actualClass) {
                    return typeMap.getOrDefault(actualClass, "string");
                }
            }
        }
        return "string";
    }

    public static String resolveFormat(String enumValueType) {
        return switch (enumValueType) {
            case "integer" -> "int32";
            case "long" -> "int64";
            case "number" -> "double";
            default -> enumValueType;
        };
    }

    public static boolean hasRestControllerAnnotation(Class<?> clazz) {
        if (clazz == null || Object.class.equals(clazz)) {
            return false;
        }
        return clazz.isAnnotationPresent(RestController.class) || hasRestControllerAnnotation(clazz.getSuperclass());
    }

    public static Map<Object, String> getDescMap(Class<?> enumClass) {
        BaseEnum<?>[] enums = (BaseEnum<?>[])enumClass.getEnumConstants();
        return Arrays.stream(enums)
            .collect(Collectors.toMap(BaseEnum::getValue, BaseEnum::getDescription, (a, b) -> a, LinkedHashMap::new));
    }
}
