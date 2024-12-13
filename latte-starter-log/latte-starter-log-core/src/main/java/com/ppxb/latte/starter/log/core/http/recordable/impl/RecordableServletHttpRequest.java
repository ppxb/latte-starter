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



package com.ppxb.latte.starter.log.core.http.recordable.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.json.JSONUtil;
import com.ppxb.latte.starter.core.constant.StringConstants;
import com.ppxb.latte.starter.log.core.http.recordable.RecordableHttpRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * 可记录的 HTTP 请求信息适配器
 *
 * @author Andy Wilkinson (Spring Boot Actuator)
 * @author ppxb
 * @since 1.0.0
 */
public class RecordableServletHttpRequest implements RecordableHttpRequest {

    private final HttpServletRequest request;

    public RecordableServletHttpRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String getMethod() {
        return request.getMethod();
    }

    @Override
    public URI getUrl() {
        String queryString = request.getQueryString();
        if (CharSequenceUtil.isBlank(queryString)) {
            return URI.create(request.getRequestURL().toString());
        }

        try {
            StringBuilder urlBuilder = this.appendQueryString(queryString);
            return new URI(urlBuilder.toString());
        } catch (URISyntaxException e) {
            String encoded = UriUtils.encodeQuery(queryString, StandardCharsets.UTF_8);
            StringBuilder urlBuilder = this.appendQueryString(encoded);
            return URI.create(urlBuilder.toString());
        }
    }

    @Override
    public String getIp() {
        return JakartaServletUtil.getClientIP(request);
    }

    @Override
    public Map<String, String> getHeaders() {
        return JakartaServletUtil.getHeaderMap(request);
    }

    @Override
    public String getBody() {
        return Optional.ofNullable(WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class))
            .map(wrapper -> {
                String body = StrUtil.utf8Str(wrapper.getContentAsByteArray());
                return JSONUtil.isTypeJSON(body) ? body : null;
            })
            .orElse(null);
    }

    @Override
    public Map<String, Object> getParam() {
        String body = getBody();
        if (CharSequenceUtil.isNotBlank(body) && JSONUtil.isTypeJSON(body)) {
            return JSONUtil.toBean(body, Map.class);
        }
        return Collections.unmodifiableMap(request.getParameterMap());
    }

    /**
     * 拼接查询参数字符串
     *
     * @param queryString 查询参数字符串
     * @return 拼接后的StringBuilder对象
     */
    private StringBuilder appendQueryString(String queryString) {
        return new StringBuilder().append(request.getRequestURL())
            .append(StringConstants.QUESTION_MARK)
            .append(queryString);
    }
}
