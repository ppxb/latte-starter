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



package com.ppxb.latte.starter.extension.crud.autoconfigure;

import cn.hutool.core.util.ArrayUtil;
import com.ppxb.latte.starter.core.util.ExceptionUtils;
import com.ppxb.latte.starter.extension.crud.annotation.CrudRequestMapping;
import com.ppxb.latte.starter.extension.crud.enums.Api;
import jakarta.annotation.Nonnull;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

public class CrudRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected RequestMappingInfo getMappingForMethod(@Nonnull Method method, @Nonnull Class<?> handlerType) {
        RequestMappingInfo requestMappingInfo = super.getMappingForMethod(method, handlerType);
        if (requestMappingInfo == null) {
            return null;
        }
        // 如果没有声明 @CrudRequestMapping 注解，直接返回
        if (!handlerType.isAnnotationPresent(CrudRequestMapping.class)) {
            return requestMappingInfo;
        }
        CrudRequestMapping crudRequestMapping = handlerType.getDeclaredAnnotation(CrudRequestMapping.class);
        // 过滤 API，如果非本类中定义，且 API 列表中不包含，则忽略
        Api[] apis = crudRequestMapping.api();
        Api api = ExceptionUtils.exToNull(() -> Api.valueOf(method.getName().toUpperCase()));
        if (method.getDeclaringClass() != handlerType && !ArrayUtil.contains(apis, api)) {
            return null;
        }
        // 拼接路径（合并了 @RequestMapping 的部分能力）
        return getMappingForMethodWrapper(method, handlerType, crudRequestMapping);
    }

    private RequestMappingInfo getMappingForMethodWrapper(@Nonnull Method method,
                                                          @Nonnull Class<?> handlerType,
                                                          CrudRequestMapping crudRequestMapping) {
        RequestMappingInfo info = buildRequestMappingInfo(method);
        if (info != null) {
            RequestMappingInfo typeInfo = buildRequestMappingInfo(handlerType);
            if (typeInfo != null) {
                info = typeInfo.combine(info);
            }
            String prefix = crudRequestMapping.value();
            if (prefix != null) {
                RequestMappingInfo.BuilderConfiguration options = new RequestMappingInfo.BuilderConfiguration();
                options.setPatternParser(PathPatternParser.defaultInstance);
                info = RequestMappingInfo.paths(prefix).options(options).build().combine(info);
            }
        }
        return info;
    }

    private RequestMappingInfo buildRequestMappingInfo(AnnotatedElement element) {
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        RequestCondition<?> condition = element instanceof Class<?> clazz
            ? getCustomTypeCondition(clazz)
            : getCustomMethodCondition((Method)element);
        return requestMapping != null ? super.createRequestMappingInfo(requestMapping, condition) : null;
    }
}
