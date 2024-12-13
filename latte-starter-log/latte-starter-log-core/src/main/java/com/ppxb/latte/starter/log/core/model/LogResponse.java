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



package com.ppxb.latte.starter.log.core.model;

import com.ppxb.latte.starter.log.core.enums.Include;
import com.ppxb.latte.starter.log.core.http.recordable.RecordableHttpResponse;

import java.util.Map;
import java.util.Set;

/**
 * 响应信息
 *
 * @author ppxb
 * @since 1.0.0
 */
public class LogResponse {

    private Integer status;

    private Map<String, String> headers;

    private String body;

    private Map<String, Object> param;

    public LogResponse(RecordableHttpResponse response, Set<Include> includes) {
        this.status = response.getStatus();

        // 设置响应头
        if (includes.contains(Include.RESPONSE_HEADERS)) {
            this.headers = response.getHeaders();
        }

        // 设置响应体或参数
        if (includes.contains(Include.RESPONSE_BODY)) {
            this.body = response.getBody();
        } else if (includes.contains(Include.RESPONSE_PARAM)) {
            this.param = response.getParam();
        }
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }
}
