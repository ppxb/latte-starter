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



package com.ppxb.latte.starter.core.util;

import com.ppxb.latte.starter.core.constant.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class ExceptionUtils {

    private static final Logger log = LoggerFactory.getLogger(ExceptionUtils.class);

    private ExceptionUtils() {
    }

    /**
     * 打印异常信息，特别处理Future类型的异常
     *
     * @param runnable  可运行的任务
     * @param throwable 异常对象
     */
    public static void printException(Runnable runnable, Throwable throwable) {
        if (null == throwable && runnable instanceof Future<?> future) {
            try {
                if (future.isDone()) {
                    future.get();
                }
            } catch (CancellationException e) {
                throwable = e;
            } catch (ExecutionException e) {
                throwable = e.getCause();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (null != throwable) {
            log.error(throwable.getMessage(), throwable);
        }
    }

    /**
     * 将可能抛出异常的操作转换为返回null
     *
     * @param exSupplier 可能抛出异常的操作
     * @return 操作结果，如果发生异常返回null
     */
    public static <T> T exToNull(ExSupplier<T> exSupplier) {
        return exToDefault(exSupplier, null);
    }

    public static <T> T exToNull(ExSupplier<T> exSupplier, Consumer<Exception> exConsumer) {
        return exToDefault(exSupplier, null, exConsumer);
    }

    /**
     * 将可能抛出异常的操作转换为返回空字符串
     *
     * @param exSupplier 可能抛出异常的操作
     * @return 操作结果，如果发生异常返回空字符串
     */
    public static String exToBlank(ExSupplier<String> exSupplier) {
        return exToDefault(exSupplier, StringConstants.EMPTY);
    }

    public static <T> T exToDefault(ExSupplier<T> exSupplier, T defaultValue) {
        return exToDefault(exSupplier, defaultValue, null);
    }

    /**
     * 将可能抛出异常的操作转换为返回默认值
     *
     * @param exSupplier   可能抛出异常的操作
     * @param defaultValue 默认值
     * @param exConsumer   异常处理器
     * @return 操作结果或默认值
     */
    public static <T> T exToDefault(ExSupplier<T> exSupplier, T defaultValue, Consumer<Exception> exConsumer) {
        try {
            return exSupplier.get();
        } catch (Exception e) {
            if (null != exConsumer) {
                exConsumer.accept(e);
            }
            return defaultValue;
        }
    }

    /**
     * 可能抛出异常的Supplier接口
     *
     * @param <T> 返回值类型
     */
    @FunctionalInterface
    public interface ExSupplier<T> {
        T get() throws Exception;
    }

}
