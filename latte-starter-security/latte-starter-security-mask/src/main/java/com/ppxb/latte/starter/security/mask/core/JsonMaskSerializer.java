package com.ppxb.latte.starter.security.mask.core;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.ppxb.latte.starter.core.constant.StringConstants;
import com.ppxb.latte.starter.security.mask.annotation.JsonMask;
import com.ppxb.latte.starter.security.mask.strategy.IMaskStrategy;

import java.io.IOException;
import java.util.Objects;

public class JsonMaskSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private JsonMask jsonMask;

    public JsonMaskSerializer() {
    }

    public JsonMaskSerializer(JsonMask jsonMask) {
        this.jsonMask = jsonMask;
    }

    @Override
    public void serialize(String str,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        if (CharSequenceUtil.isBlank(str)) {
            jsonGenerator.writeString(StringConstants.EMPTY);
            return;
        }
        Class<? extends IMaskStrategy> strategyClass = jsonMask.strategy();
        IMaskStrategy maskStrategy = strategyClass != IMaskStrategy.class
                ? SpringUtil.getBean(strategyClass)
                : jsonMask.value();
        jsonGenerator.writeString(maskStrategy.mask(str, jsonMask.character(), jsonMask.left(), jsonMask.right()));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider,
                                              BeanProperty beanProperty) throws JsonMappingException {
        if (null == beanProperty) {
            return serializerProvider.findNullValueSerializer(null);
        }
        if (!Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        JsonMask jsonMaskAnnotation = ObjectUtil.defaultIfNull(
                beanProperty.getAnnotation(JsonMask.class),
                beanProperty.getContextAnnotation(JsonMask.class)
        );
        if (null == jsonMaskAnnotation) {
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return new JsonMaskSerializer(jsonMaskAnnotation);
    }
}
