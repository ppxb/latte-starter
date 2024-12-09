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



package com.ppxb.latte.starter.core.validation;

import cn.hutool.core.text.CharSequenceUtil;
import com.ppxb.latte.starter.core.exception.BadRequestException;

import java.util.function.BooleanSupplier;

public class ValidationUtils extends Validator {

    private static final Class<BadRequestException> EXCEPTION_TYPE = BadRequestException.class;

    private ValidationUtils() {
    }

    public static void throwIfNull(Object obj, String template, Object... params) {
        throwIfNull(obj, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    public static void throwIfNotNull(Object obj, String template, Object... params) {
        throwIfNotNull(obj, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    public static void throwIfEmpty(Object obj, String template, Object... params) {
        throwIfEmpty(obj, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    public static void throwIfNotEmpty(Object obj, String template, Object... params) {
        throwIfNotEmpty(obj, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    public static void throwIfBlank(CharSequence str, String template, Object... params) {
        throwIfBlank(str, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    public static void throwIfNotBlank(CharSequence str, String template, Object... params) {
        throwIfNotBlank(str, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    public static void throwIfEqual(Object obj1, Object obj2, String template, Object... params) {
        throwIfEqual(obj1, obj2, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    public static void throwIfNotEqual(Object obj1, Object obj2, String template, Object... params) {
        throwIfNotEqual(obj1, obj2, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    public static void throwIfEqualIgnoreCase(CharSequence str1, CharSequence str2, String template, Object... params) {
        throwIfEqualIgnoreCase(str1, str2, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    public static void throwIfNotEqualIgnoreCase(CharSequence str1,
                                                 CharSequence str2,
                                                 String template,
                                                 Object... params) {
        throwIfNotEqualIgnoreCase(str1, str2, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    public static void throwIf(boolean condition, String template, Object... params) {
        throwIf(condition, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    public static void throwIf(BooleanSupplier conditionSupplier, String template, Object... params) {
        throwIf(conditionSupplier, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }
}
