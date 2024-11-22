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



package com.ppxb.latte.starter.web.model;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;
import com.ppxb.latte.starter.web.autoconfigure.response.GlobalResponseProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "响应信息")
public class R<T> implements Response {

    private static final GlobalResponseProperties PROPERTIES = SpringUtil.getBean(GlobalResponseProperties.class);

    private static final String DEFAULT_SUCCESS_CODE = PROPERTIES.getDefaultSuccessCode();

    private static final String DEFAULT_SUCCESS_MSG = PROPERTIES.getDefaultSuccessMsg();

    private static final String DEFAULT_ERROR_CODE = PROPERTIES.getDefaultErrorCode();

    private static final String DEFAULT_ERROR_MSG = PROPERTIES.getDefaultErrorMsg();

    @Schema(description = "状态码")
    private String code;

    @Schema(description = "状态信息")
    private String msg;

    @Schema(description = "是否成功")
    private boolean success;

    @Schema(description = "时间戳")
    private Long timestamp = System.currentTimeMillis();

    @Schema(description = "响应数据")
    private T data;

    public R(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public R(String code, String msg, T data) {
        this(code, msg);
        this.data = data;
    }

    public static R<?> ok() {
        return new R<>(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MSG);
    }

    public static R<?> ok(Object data) {
        return new R<>(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MSG, data);
    }

    public static R<?> ok(String msg, Object data) {
        return new R<>(DEFAULT_SUCCESS_CODE, msg, data);
    }

    public static R<?> fail() {
        return new R<>(DEFAULT_ERROR_CODE, DEFAULT_ERROR_MSG);
    }

    public static R<?> fail(String code, String msg) {
        return new R<>(code, msg);
    }

    @Override
    @JsonIgnore
    public ResponseStatus getStatus() {
        return null;
    }

    @Override
    public void setStatus(ResponseStatus status) {
        this.setCode(status.getCode());
        this.setMsg(status.getMsg());
    }

    @Override
    @JsonIgnore
    public Object getPayload() {
        return null;
    }

    @Override
    public void setPayload(Object payload) {
        this.data = (T)payload;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        this.success = DEFAULT_SUCCESS_CODE.equals(code);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
