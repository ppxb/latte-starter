package com.ppxb.latte.starter.core.validation;

import cn.hutool.core.text.CharSequenceUtil;
import com.ppxb.latte.starter.core.constant.StringConstants;
import com.ppxb.latte.starter.core.exception.BusinessException;

import java.util.function.BooleanSupplier;

public class CheckUtils extends Validator {

    private static final Class<BusinessException> EXCEPTION_TYPE = BusinessException.class;

    private CheckUtils() {
    }

    public static void throwIfNotExists(Object obj, String entityName, String fieldName, Object fieldValue) {
        String message = "%s 为 [%s] 的 %s 记录已不存在".formatted(fieldName, fieldValue, CharSequenceUtil
                .replace(entityName, "DO", StringConstants.EMPTY));
        throwIfNull(obj, message, EXCEPTION_TYPE);
    }

    public static void throwIfNull(Object obj, String template, Object... params) {
        throwIfNull(obj, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    public static void throwIfNotNull(Object obj, String template, Object... params) {
        throwIfNotNull(obj, CharSequenceUtil.format(template, params), EXCEPTION_TYPE);
    }

    public static void throwIfExists(Object obj, String entityName, String fieldName, Object fieldValue) {
        String message = "%s 为 [%s] 的 %s 记录已存在".formatted(fieldName, fieldValue, entityName);
        throwIfNotNull(obj, message, EXCEPTION_TYPE);
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
