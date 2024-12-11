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

import com.ppxb.latte.starter.core.constant.PropertiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.task.ThreadPoolTaskExecutorCustomizer;
import org.springframework.boot.task.ThreadPoolTaskSchedulerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 线程池自动配置类
 *
 * @author ppxb
 * @since 1.0.0
 */
@Lazy
@AutoConfiguration
@EnableConfigurationProperties(ThreadPoolExtensionProperties.class)
public class ThreadPoolAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolAutoConfiguration.class);

    /**
     * 核心线程数，默认为 CPU 核心数 + 1
     */
    @Value("${spring.task.execution.pool.core-size:#{T(java.lang.Runtime).getRuntime().availableProcessors() + 1}}")
    private int corePoolSize;

    /**
     * 最大线程数，默认为 CPU 核心数 * 2
     */
    @Value("${spring.task.execution.pool.max-size:#{T(java.lang.Runtime).getRuntime().availableProcessors() * 2}}")
    private int maxPoolSize;

    /**
     * 配置异步任务线程池
     * <p>
     * 提供以下配置：
     * <ul>
     * <li>核心线程数</li>
     * <li>最大线程数</li>
     * <li>拒绝策略</li>
     * </ul>
     *
     * @param properties 线程池扩展配置属性
     * @return 线程池定制器
     */
    @Bean
    @ConditionalOnProperty(prefix = "string.task.execution.extension", name = PropertiesConstants.ENABLED, havingValue = "true", matchIfMissing = true)
    public ThreadPoolTaskExecutorCustomizer threadPoolTaskExecutorCustomizer(ThreadPoolExtensionProperties properties) {
        return executor -> {
            executor.setCorePoolSize(corePoolSize);
            executor.setMaxPoolSize(maxPoolSize);
            executor.setRejectedExecutionHandler(properties.getExecution()
                .getRejectedPolicy()
                .getRejectedExecutionHandler());
            log.debug("[Latte Starter] - Auto Configuration 'TaskExecutor' completed initialization.");
        };
    }

    /**
     * 定时任务线程池配置类
     *
     * @author ppxb
     * @since 1.0.0
     */
    @EnableScheduling
    @ConditionalOnProperty(prefix = "spring.task.scheduling.extension", name = PropertiesConstants.ENABLED, havingValue = "true", matchIfMissing = true)
    public static class TaskSchedulerConfiguration {

        @Bean
        public ThreadPoolTaskSchedulerCustomizer threadPoolTaskSchedulerCustomizer(ThreadPoolExtensionProperties properties) {
            return executor -> {
                executor.setRejectedExecutionHandler(properties.getScheduling()
                    .getRejectedPolicy()
                    .getRejectedExecutionHandler());
                log.debug("[Latte Starter] - Auto Configuration 'TaskScheduler' completed initialization.");
            };
        }
    }
}
