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



package com.ppxb.latte.starter.web.autoconfigure.response;

import cn.hutool.core.util.ClassUtil;
import com.ppxb.latte.starter.apidoc.util.DocUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springdoc.core.parsers.ReturnTypeParser;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Type;

public class ApiDocGlobalResponseHandler implements ReturnTypeParser {

    private final GlobalResponseProperties globalResponseProperties;

    private final Class<Object> responseClass;

    public ApiDocGlobalResponseHandler(GlobalResponseProperties globalResponseProperties) {
        this.globalResponseProperties = globalResponseProperties;
        this.responseClass = ClassUtil.loadClass(globalResponseProperties.getResponseClassFullName());
    }

    @Override
    public Type getReturnType(MethodParameter methodParameter) {
        Type returnType = ReturnTypeParser.super.getReturnType(methodParameter);
        if (!DocUtils.hasRestControllerAnnotation(methodParameter.getContainingClass()) || returnType.getTypeName()
            .contains(globalResponseProperties.getResponseClassFullName())) {
            return returnType;
        }
        if (returnType == void.class || returnType == Void.class) {
            return TypeUtils.parameterize(responseClass, Void.class);
        }
        return TypeUtils.parameterize(responseClass, returnType);
    }
}
