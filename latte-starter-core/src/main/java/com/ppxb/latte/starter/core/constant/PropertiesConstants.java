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

public class PropertiesConstants {

    public static final String LATTE_STARTER = "latte-starter";

    public static final String ENABLED = "enabled";

    public static final String WEB = LATTE_STARTER + StringConstants.DOT + "web";

    public static final String WEB_CORS = WEB + StringConstants.DOT + "cors";

    public static final String WEB_RESPONSE = WEB + StringConstants.DOT + "response";

    public static final String WEB_XSS = WEB + StringConstants.DOT + "xss";

    public static final String WEB_TRACE = WEB + StringConstants.DOT + "trace";

    public static final String LOG = LATTE_STARTER + StringConstants.DOT + "log";

    public static final String STORAGE = LATTE_STARTER + StringConstants.DOT + "storage";

    public static final String STORAGE_LOCAL = STORAGE + StringConstants.DOT + "local";

    public static final String SECURITY = LATTE_STARTER + StringConstants.DOT + "security";

    public static final String SECURITY_PASSWORD = SECURITY + StringConstants.DOT + "password";

    public static final String SECURITY_CRYPTO = SECURITY + StringConstants.DOT + "crypto";

    public static final String SECURITY_LIMITER = SECURITY + StringConstants.DOT + "limiter";

    private PropertiesConstants() {
    }
}
