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



package com.ppxb.latte.starter.web.autoconfigure.trace;

import cn.hutool.core.text.CharSequenceUtil;
import com.yomahub.tlog.context.TLogContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class TLogServletFilter implements Filter {

    private final TraceProperties traceProperties;

    public TLogServletFilter(TraceProperties traceProperties) {
        this.traceProperties = traceProperties;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest servletRequest && response instanceof HttpServletResponse servletResponse) {
            try {
                TLogWebCommon.loadInstance().preHandler(servletRequest);
                String traceIdName = traceProperties.getTraceIdName();
                if (CharSequenceUtil.isNotBlank(traceIdName)) {
                    servletResponse.addHeader(traceIdName, TLogContext.getTraceId());
                }
                chain.doFilter(request, response);
            } finally {
                TLogWebCommon.loadInstance().afterCompletion();
            }
            return;
        }
        chain.doFilter(request, response);
    }
}
