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



package com.ppxb.latte.starter.json.jackson.serializer;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.type.ClassKey;

public class SimpleDeserializersWrapper extends SimpleDeserializers {

    @Override
    public JsonDeserializer<?> findEnumDeserializer(Class<?> type,
                                                    DeserializationConfig config,
                                                    BeanDescription beanDesc) throws JsonMappingException {
        JsonDeserializer<?> deserializer = super.findEnumDeserializer(type, config, beanDesc);
        if (null != deserializer) {
            return deserializer;
        }
        for (Class<?> typeInterface : type.getInterfaces()) {
            deserializer = this._classMappings.get(new ClassKey(typeInterface));
            if (null != deserializer) {
                return deserializer;
            }
        }
        return null;
    }
}
