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


package com.ppxb.latte.starter.core.autoconfigure.project;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 项目基础配置属性类。
 * <p>
 * 该类提供项目的基本信息配置，所有属性以"project"为前缀。
 * <p>
 * 配置示例 (application.yml):
 * <pre>{@code
 * project:
 *   name: "Latte Starter"
 *   app-name: "latte-starter"
 *   version: "1.0.0"
 *   description: "一个基于Spring Boot的快速开发框架"
 *   url: "https://github.com/ppxb/latte-starter"
 *   base-package: "com.ppxb.latte"
 *   production: false
 * }</pre>
 *
 * @author ppxb
 * @since 1.0.0
 */
@ConfigurationProperties("project")
public class ProjectProperties {

    /**
     * 项目名称
     */
    private String name;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 项目版本号
     */
    private String version;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 项目URL
     */
    private String url;

    /**
     * 项目基础包名
     * <p>
     * 项目的根包路径，用于组件扫描等配置，如"com.ppxb.latte"。
     */
    private String basePackage;

    /**
     * 是否为生产环境
     */
    private boolean production;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public boolean isProduction() {
        return production;
    }

    public void setProduction(boolean production) {
        this.production = production;
    }
}
