package com.ppxb.latte.starter.security.mask.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ppxb.latte.starter.core.constant.CharConstants;
import com.ppxb.latte.starter.security.mask.core.JsonMaskSerializer;
import com.ppxb.latte.starter.security.mask.enums.MaskType;
import com.ppxb.latte.starter.security.mask.strategy.IMaskStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = JsonMaskSerializer.class)
public @interface JsonMask {

    MaskType value() default MaskType.CUSTOM;

    Class<? extends IMaskStrategy> strategy() default IMaskStrategy.class;

    int left() default 0;

    int right() default 0;

    char character() default CharConstants.ASTERISK;
}
