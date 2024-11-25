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

import cn.hutool.core.text.CharSequenceUtil;
import com.ppxb.latte.starter.core.util.ExceptionUtils;
import com.ppxb.latte.starter.core.util.IpUtils;
import com.ppxb.latte.starter.log.core.enums.Include;
import com.ppxb.latte.starter.web.util.ServletUtils;
import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.util.Map;
import java.util.Set;

public class LogRequest {

    private String method;

    private URI url;

    private String ip;

    private Map<String, String> headers;

    private String body;

    private Map<String, Object> param;

    private String address;

    private String browser;

    private String os;

    public LogRequest(RecordableHttpRequest request, Set<Include> includes) {
        this.method = request.getMethod();
        this.url = request.getUrl();
        this.ip = request.getIp();
        this.headers = includes.contains(Include.REQUEST_HEADERS) ? request.getHeaders() : null;
        if (includes.contains(Include.REQUEST_BODY)) {
            this.body = request.getBody();
        }
        if (includes.contains(Include.REQUEST_PARAM)) {
            this.param = request.getParam();
        }
        this.address = includes.contains(Include.IP_ADDRESS)
            ? ExceptionUtils.exToNull(() -> IpUtils.getIpv4Address(this.ip))
            : null;
        if (null == this.headers) {
            return;
        }

        String userAgentString = this.headers.entrySet()
            .stream()
            .filter(header -> HttpHeaders.USER_AGENT.equalsIgnoreCase(header.getKey()))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(null);
        if (CharSequenceUtil.isNotBlank(userAgentString)) {
            this.browser = includes.contains(Include.BROWSER) ? ServletUtils.getBrowser(userAgentString) : null;
            this.os = includes.contains(Include.OS) ? ServletUtils.getOs(userAgentString) : null;
        }
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }
}
