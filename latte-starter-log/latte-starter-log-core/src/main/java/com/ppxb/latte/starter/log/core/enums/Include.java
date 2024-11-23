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



package com.ppxb.latte.starter.log.core.enums;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public enum Include {

    /**
     * 描述
     */
    DESCRIPTION,

    /**
     * 模块
     */
    MODULE,

    /**
     * 请求头
     */
    REQUEST_HEADERS,

    /**
     * 请求体（如包含请求体，则请求参数无效）
     */
    REQUEST_BODY,

    /**
     * 请求参数
     */
    REQUEST_PARAM,

    /**
     * IP
     */
    IP_ADDRESS,

    /**
     * 浏览器
     */
    BROWSER,

    /**
     * 操作系统
     */
    OS,

    /**
     * 响应头
     */
    RESPONSE_HEADERS,

    /**
     * 响应体（如包含响应体，则响应参数无效）
     */
    RESPONSE_BODY,

    /**
     * 响应参数
     */
    RESPONSE_PARAM;

    private static final Set<Include> DEFAULT_INCLUDES;

    static {
        Set<Include> defaultIncludes = new LinkedHashSet<>();
        defaultIncludes.add(Include.REQUEST_HEADERS);
        defaultIncludes.add(Include.RESPONSE_HEADERS);
        defaultIncludes.add(Include.REQUEST_PARAM);
        defaultIncludes.add(Include.RESPONSE_PARAM);
        DEFAULT_INCLUDES = Collections.unmodifiableSet(defaultIncludes);
    }

    public static Set<Include> defaultIncludes() {
        return DEFAULT_INCLUDES;
    }
}
