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

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.ppxb.latte.starter.core.constant.StringConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collection;
import java.util.Map;

public class ServletUtils {

    private static final String USER_AGENT_HEADER = "User-Agent";

    private ServletUtils() {
    }

    public static String getBrowser(HttpServletRequest request) {
        if (null == request) {
            return null;
        }
        return getBrowser(request.getHeader(USER_AGENT_HEADER));
    }

    public static String getBrowser(String userAgentString) {
        UserAgent userAgent = UserAgentUtil.parse(userAgentString);
        return userAgent.getBrowser().getName() + StringConstants.SPACE + userAgent.getVersion();
    }

    public static String getOs(HttpServletRequest request) {
        if (null == request) {
            return null;
        }
        return getOs(request.getHeader(USER_AGENT_HEADER));
    }

    public static String getOs(String userAgentString) {
        UserAgent userAgent = UserAgentUtil.parse(userAgentString);
        return userAgent.getOs().getName();
    }

    public static Map<String, String> getHeaderMap(HttpServletResponse response) {
        Collection<String> headerNames = response.getHeaderNames();
        Map<String, String> headerMap = MapUtil.newHashMap(headerNames.size(), true);
        for (String headerName : headerNames) {
            headerMap.put(headerName, response.getHeader(headerName));
        }
        return headerMap;
    }
}
