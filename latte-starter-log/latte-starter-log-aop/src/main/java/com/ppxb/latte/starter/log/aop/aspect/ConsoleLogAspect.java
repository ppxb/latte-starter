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

import com.alibaba.ttl.TransmittableThreadLocal;
import com.ppxb.latte.starter.log.aop.autoconfigure.LogProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.time.Instant;

@Aspect
public class ConsoleLogAspect {

    private static final Logger log = LoggerFactory.getLogger(ConsoleLogAspect.class);

    private final LogProperties logProperties;

    private final TransmittableThreadLocal<Instant> timeTTL = new TransmittableThreadLocal<>();

    public ConsoleLogAspect(LogProperties logProperties) {
        this.logProperties = logProperties;
    }

    @Pointcut("execution(* *..controller.*.*(..)) || execution(* *..*Controller.*(..))")
    public void controllerLayer() {
    }

    @Before(value = "controllerLayer()")
    public void doBefore() {
        if (Boolean.TRUE.equals(logProperties.getIsPrint())) {
            Instant startTime = Instant.now();
            ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                log.info("[{}] {}", request.getMethod(), request.getRequestURI());
            }
            timeTTL.set(startTime);
        }
    }

    @After(value = "controllerLayer()")
    public void afterAdvice() {
        if (Boolean.TRUE.equals(logProperties.getIsPrint())) {
            Instant endTime = Instant.now();
            ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            HttpServletRequest request = attributes.getRequest();
            HttpServletResponse response = attributes.getResponse();
            Duration timeTaken = Duration.between(timeTTL.get(), endTime);
            log.info("[{}] {} {} {}ms", request.getMethod(), request.getRequestURI(), response != null
                ? response.getStatus()
                : "N/A", timeTaken.toMillis());
        }
    }
}
