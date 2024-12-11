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



package com.ppxb.latte.starter.core.autoconfigure.threadpool;

import cn.hutool.core.util.ArrayUtil;
import com.ppxb.latte.starter.core.constant.PropertiesConstants;
import com.ppxb.latte.starter.core.exception.BaseException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.concurrent.Executor;

/**
 * 异步任务自动配置类
 *
 * @author ppxb
 * @since 1.0.0
 */
@Lazy
@AutoConfiguration
@EnableAsync(proxyTargetClass = true)
@ConditionalOnProperty(prefix = "spring.task.execution.extension", name = PropertiesConstants.ENABLED, havingValue = "true", matchIfMissing = true)
public class AsyncAutoConfiguration implements AsyncConfigurer {

    private static final Logger log = LoggerFactory.getLogger(AsyncAutoConfiguration.class);

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public AsyncAutoConfiguration(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    /**
     * 配置异步任务执行器
     *
     * @return 异步任务执行器
     */
    @Override
    public Executor getAsyncExecutor() {
        return threadPoolTaskExecutor;
    }

    /**
     * 异步任务异常处理器
     *
     * @return 异常处理器
     * @see AsyncUncaughtExceptionHandler
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            log.error("Async method {} execution failed with parameters: {}", method.getName(), ArrayUtil
                .isNotEmpty(objects) ? Arrays.toString(objects) : "none", throwable);
            // 构建错误消息并抛出业务异常
            String errorMessage = String.format("Async execution failed - method: %s, message: %s", method
                .getName(), throwable.getMessage());
            throw new BaseException(errorMessage);
        };
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Latte Starter] - Auto Configuration 'AsyncConfigurer' completed initialization.");
    }
}
