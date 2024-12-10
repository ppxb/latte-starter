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



package com.ppxb.latte.starter.core.util;

import cn.hutool.core.util.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 反射工具类
 * <p>
 * 提供了一系列反射操作的工具方法，主要用于获取类的非静态字段信息。
 * 主要功能包括：
 * <ul>
 * <li>获取类的所有非静态字段名称</li>
 * <li>获取类的所有非静态字段对象</li>
 * </ul>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 获取类的所有非静态字段名称
 * List<String> fieldNames = ReflectUtils.getNonStaticFieldsName(User.class);
 *
 * // 获取类的所有非静态字段对象
 * List<Field> fields = ReflectUtils.getNonStaticFields(User.class);
 * }</pre>
 *
 * @author ppxb
 * @since 1.0.0
 */
public class ReflectUtils {

    private ReflectUtils() {
    }

    /**
     * 获取类的所有非静态字段名称
     * <p>
     * 该方法会返回指定类中所有非静态字段的名称列表，不包括继承的字段。
     * 静态字段会被自动过滤掉。
     *
     * @param beanClass 要获取字段的类
     * @return 非静态字段名称列表
     * @throws SecurityException 如果存在安全管理器并且调用者没有权限访问这些字段
     */
    public static List<String> getNonStaticFieldsName(Class<?> beanClass) throws SecurityException {
        List<Field> nonStaticFields = getNonStaticFields(beanClass);
        return nonStaticFields.stream().map(Field::getName).collect(Collectors.toList());
    }

    /**
     * 获取类的所有非静态字段对象
     * <p>
     * 该方法会返回指定类中所有非静态字段的 Field 对象列表，不包括继承的字段。
     * 静态字段会被自动过滤掉。
     *
     * @param beanClass 要获取字段的类
     * @return 非静态字段对象列表
     * @throws SecurityException 如果存在安全管理器并且调用者没有权限访问这些字段
     */
    public static List<Field> getNonStaticFields(Class<?> beanClass) throws SecurityException {
        Field[] fields = ReflectUtil.getFields(beanClass);
        return Arrays.stream(fields).filter(f -> !Modifier.isStatic(f.getModifiers())).collect(Collectors.toList());
    }
}
