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

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.task")
public class ThreadPoolExtensionProperties {

    /**
     * 异步任务扩展配置属性
     */
    private ExecutorExtensionProperties execution = new ExecutorExtensionProperties();

    /**
     * 调度任务扩展配置属性
     */
    private SchedulerExtensionProperties scheduling = new SchedulerExtensionProperties();

    public ExecutorExtensionProperties getExecution() {
        return execution;
    }

    public void setExecution(ExecutorExtensionProperties execution) {
        this.execution = execution;
    }

    public SchedulerExtensionProperties getScheduling() {
        return scheduling;
    }

    public void setScheduling(SchedulerExtensionProperties scheduling) {
        this.scheduling = scheduling;
    }

    /**
     * 异步任务扩展配置属性
     */
    public static class ExecutorExtensionProperties {

        /**
         * 拒绝策略，默认为 CALLER_RUNS
         */
        private ThreadPoolExecutorRejectedPolicy rejectedPolicy = ThreadPoolExecutorRejectedPolicy.CALLER_RUNS;

        public ThreadPoolExecutorRejectedPolicy getRejectedPolicy() {
            return rejectedPolicy;
        }

        public void setRejectedPolicy(ThreadPoolExecutorRejectedPolicy rejectedPolicy) {
            this.rejectedPolicy = rejectedPolicy;
        }
    }

    /**
     * 调度任务扩展配置属性
     */
    public static class SchedulerExtensionProperties {

        /**
         * 拒绝策略，默认为 CALLER_RUNS
         */
        private ThreadPoolExecutorRejectedPolicy rejectedPolicy = ThreadPoolExecutorRejectedPolicy.CALLER_RUNS;

        public ThreadPoolExecutorRejectedPolicy getRejectedPolicy() {
            return rejectedPolicy;
        }

        public void setRejectedPolicy(ThreadPoolExecutorRejectedPolicy rejectedPolicy) {
            this.rejectedPolicy = rejectedPolicy;
        }
    }
}
