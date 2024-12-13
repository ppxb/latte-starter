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



package com.ppxb.latte.starter.core.validation.constraints;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;

/**
 * 枚举校验注解校验器
 *
 * @author ppxb
 * @since 1.0.0
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {

    private static final Logger log = LoggerFactory.getLogger(EnumValueValidator.class);

    private Class<? extends Enum> enumClass;

    private String[] enumValues;

    private String enumMethod;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumClass = constraintAnnotation.value();
        this.enumValues = constraintAnnotation.enumValues();
        this.enumMethod = constraintAnnotation.method();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (context == null) {
            return true;
        }
        // 优先校验 enumValues
        if (enumValues.length > 0) {
            return Arrays.asList(enumValues).contains(Convert.toStr(value));
        }

        Enum[] enumConstants = enumClass.getEnumConstants();
        if (enumConstants.length == 0) {
            return false;
        }
        if (CharSequenceUtil.isBlank(enumMethod)) {
            return findEnumValue(enumConstants, Enum::toString, Convert.toStr(value));
        }

        try {
            // 枚举类指定了方法名，则调用指定方法获取枚举值
            Method method = enumClass.getMethod(enumMethod);
            for (Enum enumConstant : enumConstants) {
                if (Convert.toStr(method.invoke(enumConstant)).equals(Convert.toStr(value))) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("An error occurred while validating the enum value, please check the @EnumValue parameter configuration.", e);
        }
        return false;
    }

    private boolean findEnumValue(Enum[] enumConstants, Function<Enum, Object> function, Object value) {
        for (Enum enumConstant : enumConstants) {
            if (function.apply(enumConstant).equals(value)) {
                return true;
            }
        }
        return false;
    }
}
