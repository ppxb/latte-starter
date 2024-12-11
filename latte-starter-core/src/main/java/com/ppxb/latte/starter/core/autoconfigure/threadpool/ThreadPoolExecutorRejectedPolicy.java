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

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池拒绝策略枚举
 *
 * @author ppxb
 * @since 1.0.0
 */
public enum ThreadPoolExecutorRejectedPolicy {

    /**
     * 中止策略（默认）
     * <p>
     * 当任务无法执行时：
     * <ul>
     * <li>直接抛出 RejectedExecutionException 异常</li>
     * <li>不执行新提交的任务</li>
     * <li>通常用于需要立即感知任务提交失败的场景</li>
     * </ul>
     */
    ABORT {
        @Override
        public RejectedExecutionHandler getRejectedExecutionHandler() {
            return new ThreadPoolExecutor.AbortPolicy();
        }
    },

    /**
     * 调用者运行策略
     * <p>
     * 当任务无法执行时：
     * <ul>
     * <li>由提交任务的线程执行该任务</li>
     * <li>可以减缓新任务的提交速度</li>
     * <li>适用于任务不能丢失但可以接受延迟的场景</li>
     * </ul>
     */
    CALLER_RUNS {
        @Override
        public RejectedExecutionHandler getRejectedExecutionHandler() {
            return new ThreadPoolExecutor.CallerRunsPolicy();
        }
    },

    /**
     * 丢弃策略
     * <p>
     * 当任务无法执行时：
     * <ul>
     * <li>直接丢弃新提交的任务</li>
     * <li>不抛出任何异常</li>
     * <li>适用于任务可以被安全丢弃的场景</li>
     * </ul>
     */
    DISCARD {
        @Override
        public RejectedExecutionHandler getRejectedExecutionHandler() {
            return new ThreadPoolExecutor.DiscardPolicy();
        }
    },

    /**
     * 丢弃最旧任务策略
     * <p>
     * 当任务无法执行时：
     * <ul>
     * <li>丢弃队列中最旧的任务</li>
     * <li>尝试提交新任务</li>
     * <li>适用于优先处理新任务的场景</li>
     * </ul>
     */
    DISCARD_OLDEST {
        @Override
        public RejectedExecutionHandler getRejectedExecutionHandler() {
            return new ThreadPoolExecutor.DiscardOldestPolicy();
        }
    };

    /**
     * 获取拒绝处理器
     *
     * @return 对应策略的拒绝处理器实现
     * @see RejectedExecutionHandler
     */
    public abstract RejectedExecutionHandler getRejectedExecutionHandler();
}
