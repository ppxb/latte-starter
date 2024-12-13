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



package com.ppxb.latte.starter.log.interceptor.handler;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.ppxb.latte.starter.log.core.dao.LogDao;
import com.ppxb.latte.starter.log.core.enums.Include;
import com.ppxb.latte.starter.log.core.http.recordable.impl.RecordableServletHttpRequest;
import com.ppxb.latte.starter.log.core.http.recordable.impl.RecordableServletHttpResponse;
import com.ppxb.latte.starter.log.core.model.LogRecord;
import com.ppxb.latte.starter.log.interceptor.annotation.Log;
import com.ppxb.latte.starter.log.interceptor.autoconfigure.LogProperties;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class LogInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LogInterceptor.class);

    private final LogDao logDao;

    private final LogProperties logProperties;

    /**
     * 用于存储请求开始时间的ThreadLocal
     */
    private final TransmittableThreadLocal<Instant> timeTtl = new TransmittableThreadLocal<>();

    /**
     * 用于存储日志记录的ThreadLocal
     */
    private final TransmittableThreadLocal<LogRecord.Started> logTtl = new TransmittableThreadLocal<>();

    public LogInterceptor(LogDao logDao, LogProperties logProperties) {
        this.logDao = logDao;
        this.logProperties = logProperties;
    }

    /**
     * 请求预处理，在Controller处理之前执行
     *
     * @param request  当前HTTP请求
     * @param response 当前HTTP响应
     * @param handler  选择的处理器
     * @return true表示继续处理，false表示中断处理
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        Instant startTime = Instant.now();
        if (Boolean.TRUE.equals(logProperties.getIsPrint())) {
            log.info("[{}] {}", request.getMethod(), request.getRequestURI());
            timeTtl.set(startTime);
        }

        if (this.isRequestRecord(handler, request)) {
            LogRecord.Started startedLogRecord = LogRecord.start(startTime, new RecordableServletHttpRequest(request));
            logTtl.set(startedLogRecord);
        }
        return true;
    }

    /**
     * 请求完成后的处理，在视图渲染之后执行
     *
     * @param request  当前HTTP请求
     * @param response 当前HTTP响应
     * @param handler  选择的处理器
     * @param ex       处理过程中发生的异常
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {
        try {
            Instant endTime = Instant.now();
            if (Boolean.TRUE.equals(logProperties.getIsPrint())) {
                Duration timeTaken = Duration.between(timeTtl.get(), endTime);
                log.info("[{}] {} {} {}ms", request.getMethod(), request.getRequestURI(), response
                    .getStatus(), timeTaken.toMillis());
            }
            LogRecord.Started startedLogRecord = logTtl.get();
            if (null == startedLogRecord) {
                return;
            }
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            Log methodLog = handlerMethod.getMethodAnnotation(Log.class);
            Log classLog = handlerMethod.getBeanType().getDeclaredAnnotation(Log.class);
            Set<Include> includeSet = this.getIncludes(methodLog, classLog);
            LogRecord finishedLogRecord = startedLogRecord
                .finish(endTime, new RecordableServletHttpResponse(response, response.getStatus()), includeSet);
            // 记录日志描述
            if (includeSet.contains(Include.DESCRIPTION)) {
                this.logDescription(finishedLogRecord, methodLog, handlerMethod);
            }
            // 记录所属模块
            if (includeSet.contains(Include.MODULE)) {
                this.logModule(finishedLogRecord, methodLog, classLog, handlerMethod);
            }
            logDao.add(finishedLogRecord);
        } catch (Exception e) {
            log.error("Logging http log occurred an error: {}.", ex.getMessage(), ex);
        } finally {
            timeTtl.remove();
            logTtl.remove();
        }
    }

    private Set<Include> getIncludes(Log methodLog, Log classLog) {
        Set<Include> includeSet = new HashSet<>(logProperties.getIncludes());
        if (null != classLog) {
            this.processInclude(includeSet, classLog);
        }
        if (null != methodLog) {
            this.processInclude(includeSet, methodLog);
        }
        return includeSet;
    }

    private void processInclude(Set<Include> includes, Log logAnnotation) {
        Include[] includeArr = logAnnotation.includes();
        if (includeArr.length > 0) {
            includes.addAll(Set.of(includeArr));
        }
        Include[] excludeArr = logAnnotation.excludes();
        if (excludeArr.length > 0) {
            includes.removeAll(Set.of(excludeArr));
        }
    }

    private void logDescription(LogRecord logRecord, Log methodLog, HandlerMethod handlerMethod) {
        // 例如：@Log("新增部门") -> 新增部门
        if (null != methodLog && CharSequenceUtil.isNotBlank(methodLog.value())) {
            logRecord.setDescription(methodLog.value());
            return;
        }
        // 例如：@Operation(summary="新增部门") -> 新增部门
        Operation methodOperation = handlerMethod.getMethodAnnotation(Operation.class);
        if (null != methodOperation) {
            logRecord.setDescription(CharSequenceUtil.blankToDefault(methodOperation.summary(), "请在该接口方法上指定日志描述"));
        }
    }

    private void logModule(LogRecord logRecord, Log methodLog, Log classLog, HandlerMethod handlerMethod) {
        // 例如：@Log(module = "部门管理") -> 部门管理
        if (null != methodLog && CharSequenceUtil.isNotBlank(methodLog.module())) {
            logRecord.setModule(methodLog.module());
            return;
        }
        if (null != classLog && CharSequenceUtil.isNotBlank(classLog.module())) {
            logRecord.setModule(classLog.module());
            return;
        }
        // 例如：@Tag(name = "部门管理") -> 部门管理
        Tag classTag = handlerMethod.getBeanType().getDeclaredAnnotation(Tag.class);
        if (null != classTag) {
            String name = classTag.name();
            logRecord.setModule(CharSequenceUtil.blankToDefault(name, "请在该接口类上指定所属模块"));
        }
    }

    private boolean isRequestRecord(Object handler, HttpServletRequest request) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return false;
        }
        // 如果接口匹配排除列表，不记录日志
        if (logProperties.isMatch(request.getRequestURI())) {
            return false;
        }
        // 如果接口被隐藏，不记录日志
        Operation methodOperation = handlerMethod.getMethodAnnotation(Operation.class);
        if (null != methodOperation && methodOperation.hidden()) {
            return false;
        }
        Hidden methodHidden = handlerMethod.getMethodAnnotation(Hidden.class);
        if (null != methodHidden) {
            return false;
        }
        Class<?> handlerBeanType = handlerMethod.getBeanType();
        if (null != handlerBeanType.getDeclaredAnnotation(Hidden.class)) {
            return false;
        }
        // 如果接口方法或类上有 @Log 注解，且要求忽略该接口，则不记录日志
        Log methodLog = handlerMethod.getMethodAnnotation(Log.class);
        if (null != methodLog && methodLog.ignore()) {
            return false;
        }
        Log classLog = handlerBeanType.getDeclaredAnnotation(Log.class);
        return null == classLog || !classLog.ignore();
    }
}
