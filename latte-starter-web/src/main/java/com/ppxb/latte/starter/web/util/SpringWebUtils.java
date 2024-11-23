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



package com.ppxb.latte.starter.web.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.ppxb.latte.starter.core.constant.StringConstants;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.http.server.PathContainer;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SpringWebUtils {

    private SpringWebUtils() {
    }

    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    private static ServletRequestAttributes getRequestAttributes() {
        return (ServletRequestAttributes)Objects.requireNonNull(RequestContextHolder.getRequestAttributes());
    }

    public static boolean isMatch(String path, List<String> patterns) {
        return patterns.stream().anyMatch(pattern -> isMatch(path, pattern));
    }

    public static boolean isMatch(String path, String... patterns) {
        return Arrays.stream(patterns).anyMatch(pattern -> isMatch(path, pattern));
    }

    public static boolean isMatch(String path, String pattern) {
        PathPattern pathPattern = PathPatternParser.defaultInstance.parse(pattern);
        PathContainer pathContainer = PathContainer.parsePath(path);
        return pathPattern.matches(pathContainer);
    }

    public static void registerResourceHandler(Map<String, String> handlerMap) {
        ApplicationContext applicationContext = SpringUtil.getApplicationContext();
        Map<String, Object> oldHandlerMap = getResourceHandlerMap();
        ServletContext servletContext = applicationContext.getBean(ServletContext.class);
        ContentNegotiationManager contentNegotiationManager = applicationContext
            .getBean("mvcContentNegotiationManager", ContentNegotiationManager.class);
        UrlPathHelper urlPathHelper = applicationContext.getBean("mvcUrlPathHelper", UrlPathHelper.class);
        ResourceHandlerRegistry resourceHandlerRegistry = new ResourceHandlerRegistry(applicationContext, servletContext, contentNegotiationManager, urlPathHelper);

        handlerMap.forEach((path, location) -> {
            String pathPattern = CharSequenceUtil.appendIfMissing(path, StringConstants.PATH_PATTERN);
            String resourceLocation = CharSequenceUtil.appendIfMissing(location, StringConstants.SLASH);

            oldHandlerMap.remove(pathPattern);
            resourceHandlerRegistry.addResourceHandler(pathPattern).addResourceLocations("file:" + resourceLocation);
        });

        HandlerMapping resourceHandlerMapping = SpringUtil.getBean("resourceHandlerMapping", HandlerMapping.class);
        Map<String, ?> additionalUrlMap = ReflectUtil
            .<SimpleUrlHandlerMapping>invoke(resourceHandlerRegistry, "getHandlerMapping")
            .getUrlMap();
        ReflectUtil.invoke(resourceHandlerMapping, "registerHandlers", additionalUrlMap);
    }

    public static void deRegisterResourceHandler(Map<String, String> handlerMap) {
        Map<String, Object> oldHandlerMap = getResourceHandlerMap();
        handlerMap.keySet()
            .stream()
            .map(key -> CharSequenceUtil.appendIfMissing(key, StringConstants.PATH_PATTERN))
            .forEach(oldHandlerMap::remove);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getResourceHandlerMap() {
        HandlerMapping mapping = SpringUtil.getBean("resourceHandlerMapping", HandlerMapping.class);
        return (Map<String, Object>)ReflectUtil.getFieldValue(mapping, "handlerMap");
    }
}
