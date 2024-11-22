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



package com.ppxb.latte.starter.apidoc.autoconfigure;

import io.swagger.v3.oas.models.Components;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * SpringDoc扩展配置属性类。
 * <p>
 * 该类提供了SpringDoc（OpenAPI/Swagger）文档的扩展配置选项，
 * 主要用于配置API文档的安全方案、响应定义等组件。
 * 所有属性都以 "springdoc" 为前缀。
 *
 * @author ppxb
 * @see Components
 * @since 1.0.0
 */
@ConfigurationProperties("springdoc")
public class SpringDocExtensionProperties {

    /**
     * OpenAPI组件配置
     * <p>
     * 包含安全方案、响应定义、请求体定义等组件配置。
     * 使用 {@link NestedConfigurationProperty} 注解表示这是一个嵌套的配置属性。
     */
    @NestedConfigurationProperty
    private Components components;

    public Components getComponents() {
        return components;
    }

    public void setComponents(Components components) {
        this.components = components;
    }
}
