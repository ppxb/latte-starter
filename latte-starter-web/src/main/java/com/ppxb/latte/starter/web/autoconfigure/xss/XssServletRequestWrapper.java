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



package com.ppxb.latte.starter.web.autoconfigure.xss;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.http.Method;
import com.ppxb.latte.starter.core.constant.StringConstants;
import com.ppxb.latte.starter.web.enums.XssMode;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class XssServletRequestWrapper extends HttpServletRequestWrapper {

    private final XssProperties xssProperties;

    private String body = "";

    public XssServletRequestWrapper(HttpServletRequest request, XssProperties xssProperties) throws IOException {
        super(request);
        this.xssProperties = xssProperties;
        if (CharSequenceUtil.equalsAnyIgnoreCase(request.getMethod().toUpperCase(), Method.POST.name(), Method.PATCH
            .name(), Method.PUT.name())) {
            body = IoUtil.getReader(request.getReader()).readLine();
            if (CharSequenceUtil.isBlank(body)) {
                return;
            }
            body = handleTag(body);
        }
    }

    private static ServletInputStream getServletInputStream(String body) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    private String handleTag(String content) {
        if (CharSequenceUtil.isBlank(content)) {
            return content;
        }
        XssMode mode = xssProperties.getMode();
        if (XssMode.ESCAPE.equals(mode)) {
            List<String> reStr = ReUtil.findAllGroup0(HtmlUtil.RE_HTML_MARK, content);
            if (CollectionUtil.isNotEmpty(reStr)) {
                return content;
            }
            for (String s : reStr) {
                content = content.replace(s, EscapeUtil.escapeHtml4(s)
                    .replace(StringConstants.BACKSLASH, StringConstants.EMPTY));
            }
            return content;
        }
        return HtmlUtil.cleanHtmlTag(content);
    }

    @Override
    public BufferedReader getReader() {
        return IoUtil.toBuffered(new StringReader(body));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return getServletInputStream(body);
    }

    @Override
    public String getQueryString() {
        return handleTag(super.getQueryString());
    }

    @Override
    public String getParameter(String name) {
        return handleTag(super.getParameter(name));
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (ArrayUtil.isEmpty(values)) {
            return values;
        }
        int length = values.length;
        String[] resultValues = new String[length];
        for (int i = 0; i < length; i++) {
            resultValues[i] = handleTag(values[i]);
        }
        return resultValues;
    }
}
