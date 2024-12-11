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



package com.ppxb.latte.starter.core.constant;

/**
 * 配置属性相关常量
 *
 * @author ppxb
 * @since 1.0.0
 */
public class PropertiesConstants {

    /**
     * Latte Starter
     */
    public static final String LATTE_STARTER = "latte-starter";

    /**
     * 启用配置
     */
    public static final String ENABLED = "enabled";

    /**
     * 安全配置
     */
    public static final String SECURITY = LATTE_STARTER + StringConstants.DOT + "security";

    /**
     * 密码编解码配置
     */
    public static final String SECURITY_PASSWORD = SECURITY + StringConstants.DOT + "password";

    /**
     * 加/解密配置
     */
    public static final String SECURITY_CRYPTO = SECURITY + StringConstants.DOT + "crypto";

    /**
     * 限流配置
     */
    public static final String SECURITY_LIMITER = SECURITY + StringConstants.DOT + "limiter";

    /**
     * Web 配置
     */
    public static final String WEB = LATTE_STARTER + StringConstants.DOT + "web";

    /**
     * 跨域配置
     */
    public static final String WEB_CORS = WEB + StringConstants.DOT + "cors";

    /**
     * 响应配置
     */
    public static final String WEB_RESPONSE = WEB + StringConstants.DOT + "response";

    /**
     * 链路配置
     */
    public static final String WEB_TRACE = WEB + StringConstants.DOT + "trace";

    /**
     * XSS 配置
     */
    public static final String WEB_XSS = WEB + StringConstants.DOT + "xss";

    /**
     * 日志配置
     */
    public static final String LOG = LATTE_STARTER + StringConstants.DOT + "log";

    /**
     * 存储配置
     */
    public static final String STORAGE = LATTE_STARTER + StringConstants.DOT + "storage";

    /**
     * 本地存储配置
     */
    public static final String STORAGE_LOCAL = STORAGE + StringConstants.DOT + "local";

    /**
     * 验证码配置
     */
    public static final String CAPTCHA = LATTE_STARTER + StringConstants.DOT + "captcha";

    /**
     * 行为验证码配置
     */
    public static final String CAPTCHA_BEHAVIOR = CAPTCHA + StringConstants.DOT + "behavior";

    /**
     * 图形验证码配置
     */
    public static final String CAPTCHA_GRAPHIC = CAPTCHA + StringConstants.DOT + "graphic";

    /**
     * 消息配置
     */
    public static final String MESSAGING = LATTE_STARTER + StringConstants.DOT + "messaging";

    /**
     * WebSocket 配置
     */
    public static final String MESSAGING_WEBSOCKET = MESSAGING + StringConstants.DOT + "websocket";

    /**
     * CRUD 配置
     */
    public static final String CRUD = LATTE_STARTER + StringConstants.DOT + "crud";

    /**
     * 数据权限配置
     */
    public static final String DATA_PERMISSION = LATTE_STARTER + StringConstants.DOT + "data-permission";

    /**
     * 多租户配置
     */
    public static final String TENANT = LATTE_STARTER + StringConstants.DOT + "tenant";

    private PropertiesConstants() {
    }
}
