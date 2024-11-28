package com.ppxb.latte.starter.core.validation;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.Set;
import java.util.function.BooleanSupplier;

public class Validator {

    public static final jakarta.validation.Validator VALIDATOR = SpringUtil.getBean(jakarta.validation.Validator.class);

    protected Validator() {
    }

    protected static void throwIfNull(Object obj, String message, Class<? extends RuntimeException> exceptionType) {
        throwIf(null == obj, message, exceptionType);
    }

    protected static void throwIfNotNull(Object obj, String message, Class<? extends RuntimeException> exceptionType) {
        throwIf(null != obj, message, exceptionType);
    }

    protected static void throwIfEmpty(Object obj, String message, Class<? extends RuntimeException> exceptionType) {
        throwIf(ObjectUtil.isEmpty(obj), message, exceptionType);
    }

    protected static void throwIfNotEmpty(Object obj, String message, Class<? extends RuntimeException> exceptionType) {
        throwIf(ObjectUtil.isNotEmpty(obj), message, exceptionType);
    }

    protected static void throwIfBlank(CharSequence str,
                                       String message,
                                       Class<? extends RuntimeException> exceptionType) {
        throwIf(CharSequenceUtil.isBlank(str), message, exceptionType);
    }

    protected static void throwIfNotBlank(CharSequence str,
                                          String message,
                                          Class<? extends RuntimeException> exceptionType) {
        throwIf(CharSequenceUtil.isNotBlank(str), message, exceptionType);
    }

    protected static void throwIfEqual(Object obj1,
                                       Object obj2,
                                       String message,
                                       Class<? extends RuntimeException> exceptionType) {
        throwIf(ObjectUtil.equal(obj1, obj2), message, exceptionType);
    }

    protected static void throwIfNotEqual(Object obj1,
                                          Object obj2,
                                          String message,
                                          Class<? extends RuntimeException> exceptionType) {
        throwIf(ObjectUtil.notEqual(obj1, obj2), message, exceptionType);
    }

    protected static void throwIfEqualIgnoreCase(CharSequence str1,
                                                 CharSequence str2,
                                                 String message,
                                                 Class<? extends RuntimeException> exceptionType) {
        throwIf(CharSequenceUtil.equalsIgnoreCase(str1, str2), message, exceptionType);
    }

    protected static void throwIfNotEqualIgnoreCase(CharSequence str1,
                                                    CharSequence str2,
                                                    String message,
                                                    Class<? extends RuntimeException> exceptionType) {
        throwIf(!CharSequenceUtil.equalsIgnoreCase(str1, str2), message, exceptionType);
    }

    protected static void throwIf(boolean condition, String message, Class<? extends RuntimeException> exceptionType) {
        if (condition) {
            throw ReflectUtil.newInstance(exceptionType, message);
        }
    }

    protected static void throwIf(BooleanSupplier conditionSupplier,
                                  String message,
                                  Class<? extends RuntimeException> exceptionType) {
        if (null != conditionSupplier && conditionSupplier.getAsBoolean()) {
            throw ReflectUtil.newInstance(exceptionType, message);
        }
    }

    public static void validate(Object obj, Class<?>... groups) {
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(obj, groups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
