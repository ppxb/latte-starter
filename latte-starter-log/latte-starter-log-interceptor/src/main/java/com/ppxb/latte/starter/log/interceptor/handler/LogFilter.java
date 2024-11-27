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



package com.ppxb.latte.starter.log.interceptor.handler;

import cn.hutool.extra.spring.SpringUtil;
import com.ppxb.latte.starter.log.interceptor.autoconfigure.LogProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class LogFilter extends OncePerRequestFilter implements Ordered {

    private final LogProperties logProperties;

    public LogFilter(LogProperties logProperties) {
        this.logProperties = logProperties;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 10;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (!this.shouldFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean isMatch = logProperties.isMatch(request.getRequestURI());
        boolean isResponseWrapped = false;
        try {
            // 包装请求
            if (!isMatch && !(request instanceof ContentCachingRequestWrapper)) {
                request = new ContentCachingRequestWrapper(request);
            }
            // 包装响应
            if (!isMatch && !(response instanceof ContentCachingResponseWrapper)) {
                response = new ContentCachingResponseWrapper(response);
                isResponseWrapped = true;
            }
            filterChain.doFilter(request, response);
        } finally {
            // 确保响应内容被正确写回
            if (isResponseWrapped) {
                updateResponse(response);
            }
        }
    }

    private boolean shouldFilter(HttpServletRequest request) {
        if (!isRequestValid(request)) {
            return false;
        }
        ServerProperties serverProperties = SpringUtil.getBean(ServerProperties.class);
        return !request.getRequestURI().equals(serverProperties.getError().getPath());
    }

    private boolean isRequestValid(HttpServletRequest request) {
        try {
            new URI(request.getRequestURL().toString());
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private void updateResponse(HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper wrapper = WebUtils
            .getNativeResponse(response, ContentCachingResponseWrapper.class);
        Objects.requireNonNull(wrapper).copyBodyToResponse();
    }
}
