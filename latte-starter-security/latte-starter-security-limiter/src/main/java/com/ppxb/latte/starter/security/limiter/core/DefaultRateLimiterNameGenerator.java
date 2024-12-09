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



package com.ppxb.latte.starter.security.limiter.core;

import cn.hutool.core.util.ClassUtil;
import com.ppxb.latte.starter.core.constant.StringConstants;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultRateLimiterNameGenerator implements RateLimiterNameGenerator {

    protected final ConcurrentHashMap<Method, String> nameMap = new ConcurrentHashMap<>();

    @Override
    public String generate(Object target, Method method, Object... args) {
        return nameMap.computeIfAbsent(method, key -> {
            StringBuilder sb = new StringBuilder();
            String className = method.getDeclaringClass().getName();
            sb.append(ClassUtil.getShortClassName(className));
            sb.append(StringConstants.DOT);
            sb.append(method.getName());
            sb.append(StringConstants.ROUND_BRACKET_START);
            for (Class<?> clazz : method.getParameterTypes()) {
                getDescriptor(sb, clazz);
            }
            sb.append(StringConstants.ROUND_BRACKET_END);
            return sb.toString();
        });
    }

    private void getDescriptor(StringBuilder sb, Class<?> typeClass) {
        Class<?> clazz = typeClass;
        while (true) {
            if (clazz.isPrimitive()) {
                sb.append(getPrimitiveChar(clazz));
                return;
            } else if (clazz.isArray()) {
                sb.append(StringConstants.BRACKET_START);
                clazz = clazz.getComponentType();
            } else {
                sb.append("L");
                String name = clazz.getName();
                name = ClassUtil.getShortClassName(name);
                sb.append(name);
                sb.append(StringConstants.SEMICOLON);
                return;
            }
        }
    }

    private char getPrimitiveChar(Class<?> clazz) {
        char c;
        if (clazz == Integer.TYPE) {
            c = 'I';
        } else if (clazz == Void.TYPE) {
            c = 'V';
        } else if (clazz == Boolean.TYPE) {
            c = 'Z';
        } else if (clazz == Byte.TYPE) {
            c = 'B';
        } else if (clazz == Character.TYPE) {
            c = 'C';
        } else if (clazz == Short.TYPE) {
            c = 'S';
        } else if (clazz == Double.TYPE) {
            c = 'D';
        } else if (clazz == Float.TYPE) {
            c = 'F';
        } else {
            c = 'J';
        }
        return c;
    }
}
