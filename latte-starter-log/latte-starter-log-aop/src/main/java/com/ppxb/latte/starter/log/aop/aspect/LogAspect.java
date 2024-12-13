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



package com.ppxb.latte.starter.log.aop.aspect;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.ppxb.latte.starter.log.aop.annotation.Log;
import com.ppxb.latte.starter.log.aop.autoconfigure.LogProperties;
import com.ppxb.latte.starter.log.core.dao.LogDao;
import com.ppxb.latte.starter.log.core.enums.Include;
import com.ppxb.latte.starter.log.core.http.recordable.impl.RecordableServletHttpRequest;
import com.ppxb.latte.starter.log.core.http.recordable.impl.RecordableServletHttpResponse;
import com.ppxb.latte.starter.log.core.model.LogRecord;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * 日志切面
 *
 * @author ppxb
 * @since 1.0.0
 **/
@Aspect
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    private final LogDao logDao;

    private final LogProperties logProperties;

    private final TransmittableThreadLocal<Instant> timeTTL = new TransmittableThreadLocal<>();

    private final TransmittableThreadLocal<LogRecord.Started> logTTL = new TransmittableThreadLocal<>();

    public LogAspect(LogDao logDao, LogProperties logProperties) {
        this.logDao = logDao;
        this.logProperties = logProperties;
    }

    @Pointcut(value = "@annotation(com.ppxb.latte.starter.log.aop.annotation.Log)")
    public void pointcutService() {
    }

    @Before(value = "pointcutService()")
    public void doBefore() {
        Instant start = Instant.now();
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            LogRecord.Started logRecordStarted = LogRecord.start(start, new RecordableServletHttpRequest(request));
            logTTL.set(logRecordStarted);
        }
    }

    @After(value = "pointcutService()")
    public void afterAdvice(JoinPoint joinPoint) {
        handleAfterCompletion(joinPoint, null);
    }

    @AfterThrowing(pointcut = "pointcutService()", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, Exception exception) {
        handleAfterCompletion(joinPoint, exception);
    }

    private void handleAfterCompletion(JoinPoint joinPoint, Exception exception) {
        try {
            Instant endTime = Instant.now();
            ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }

            HttpServletResponse response = attributes.getResponse();
            LogRecord.Started logRecordStarted = logTTL.get();
            if (logRecordStarted == null) {
                return;
            }

            MethodSignature signature = (MethodSignature)joinPoint.getSignature();
            Method method = signature.getMethod();
            Class<?> targetClass = joinPoint.getTarget().getClass();

            Log methodLog = method.getAnnotation(Log.class);
            Log classLog = targetClass.getAnnotation(Log.class);
            // 获取日志包含信息
            Set<Include> includeSet = getIncludes(methodLog, classLog);
            // 完成日志记录
            LogRecord finishLogRecord = logRecordStarted
                .finish(endTime, new RecordableServletHttpResponse(response, response.getStatus()), includeSet);
            if (exception != null) {
                finishLogRecord.getResponse().setStatus(1);
                finishLogRecord.setErrorMsg(StrUtil.sub(exception.getMessage(), 0, 2000));
            }

            // 记录日志描述
            if (includeSet.contains(Include.DESCRIPTION)) {
                description(finishLogRecord, methodLog);
            }

            // 记录所属模块
            if (includeSet.contains(Include.MODULE)) {
                module(finishLogRecord, methodLog, classLog);
            }
            logDao.add(finishLogRecord);
        } catch (Exception e) {
            log.error("Logging http log occurred an error: {}.", e.getMessage(), e);
        } finally {
            timeTTL.remove();
            logTTL.remove();
        }
    }

    private Set<Include> getIncludes(Log methodLog, Log classLog) {
        Set<Include> includeSet = new HashSet<>(logProperties.getIncludes());
        if (methodLog != null) {
            processInclude(includeSet, methodLog);
        }

        if (classLog != null) {
            processInclude(includeSet, classLog);
        }
        return includeSet;
    }

    private void processInclude(Set<Include> includes, Log logAnnotation) {
        Include[] includeArray = logAnnotation.includes();
        if (includeArray.length > 0) {
            includes.addAll(Set.of(includeArray));
        }

        Include[] excludeArray = logAnnotation.excludes();
        if (excludeArray.length > 0) {
            includes.removeAll(Set.of(excludeArray));
        }
    }

    private void description(LogRecord record, Log methodLog) {
        if (methodLog != null && CharSequenceUtil.isNotBlank(methodLog.value())) {
            record.setDescription(methodLog.value());
        } else {
            record.setDescription("请在该接口方法上指定日志描述");
        }
    }

    private void module(LogRecord record, Log methodLog, Log classLog) {
        if (methodLog != null && CharSequenceUtil.isNotBlank(methodLog.module())) {
            record.setModule(methodLog.module());
            return;
        }

        if (classLog != null) {
            record.setModule(CharSequenceUtil.blankToDefault(classLog.module(), "请在该接口类上指定所属模块"));
        }
    }
}
