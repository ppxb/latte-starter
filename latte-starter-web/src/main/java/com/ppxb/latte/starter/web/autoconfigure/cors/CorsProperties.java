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


package com.ppxb.latte.starter.web.autoconfigure.cors;

import com.ppxb.latte.starter.core.constant.PropertiesConstants;
import com.ppxb.latte.starter.core.constant.StringConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * CORS (跨域资源共享) 配置属性类。
 * <p>
 * 该类提供了自定义应用程序CORS行为的配置选项。
 * 所有属性都以 "latte.web.cors" 为前缀。
 * <p>
 * 配置示例 (application.yml):
 * <pre>{@code
 * latte:
 *   web:
 *     cors:
 *       enabled: true
 *       allowed-origins:
 *         - "http://localhost:8080"
 *         - "https://example.com"
 *       allowed-methods:
 *         - "GET"
 *         - "POST"
 *       allowed-headers:
 *         - "Authorization"
 *         - "Content-Type"
 *       exposed-headers:
 *         - "X-Custom-Header"
 * }</pre>
 *
 * @author ppxb
 * @since 1.0.0
 */
@ConfigurationProperties(PropertiesConstants.WEB_CORS)
public class CorsProperties {

    /**
     * 通配符匹配的默认值，用"*"表示
     */
    private static final List<String> ALL = Collections.singletonList(StringConstants.ASTERISK);

    /**
     * 是否启用CORS支持。
     * <p>
     * 默认值为 {@code false}。
     */
    private boolean enabled = false;

    /**
     * 允许的源站列表。
     * <p>
     * 默认值为"*"，允许所有源站。在生产环境中，建议指定具体的源站地址而不是使用通配符。
     */
    private List<String> allowedOrigins = new ArrayList<>(ALL);

    /**
     * 允许的HTTP方法列表。
     * <p>
     * 默认值为"*"，允许所有方法。可以限制为特定方法，如GET、POST等。
     */
    private List<String> allowedMethods = new ArrayList<>(ALL);

    /**
     * CORS请求中允许的请求头列表。
     * <p>
     * 默认值为"*"，允许所有请求头。在生产环境中应该只配置必要的请求头。
     */
    private List<String> allowedHeaders = new ArrayList<>(ALL);

    /**
     * 允许浏览器访问的响应头列表。
     * <p>
     * 默认为空。添加需要在客户端JavaScript代码中访问的响应头。
     */
    private List<String> exposedHeaders = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public List<String> getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(List<String> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public List<String> getAllowedHeaders() {
        return allowedHeaders;
    }

    public void setAllowedHeaders(List<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    public List<String> getExposedHeaders() {
        return exposedHeaders;
    }

    public void setExposedHeaders(List<String> exposedHeaders) {
        this.exposedHeaders = exposedHeaders;
    }
}
