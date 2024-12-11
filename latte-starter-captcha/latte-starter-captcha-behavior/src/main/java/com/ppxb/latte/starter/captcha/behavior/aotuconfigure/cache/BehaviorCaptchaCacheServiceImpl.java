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



package com.ppxb.latte.starter.captcha.behavior.aotuconfigure.cache;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import com.anji.captcha.service.CaptchaCacheService;
import com.ppxb.latte.starter.cache.redisson.util.RedisUtils;
import com.ppxb.latte.starter.captcha.behavior.enums.StorageType;

import java.time.Duration;

/**
 * 行为验证码缓存服务实现类
 *
 * @author ppxb
 * @since 1.0.0
 */
public class BehaviorCaptchaCacheServiceImpl implements CaptchaCacheService {

    /**
     * 设置缓存值
     *
     * @param key      缓存键
     * @param value    缓存值
     * @param duration 过期时间（秒）
     */
    @Override
    public void set(String key, String value, long duration) {
        if (NumberUtil.isNumber(value)) {
            RedisUtils.set(key, Convert.toInt(value), Duration.ofSeconds(duration));
        } else {
            RedisUtils.set(key, value, Duration.ofSeconds(duration));
        }
    }

    /**
     * 检查缓存键是否存在
     *
     * @param key 缓存键
     * @return true 表示存在，false 表示不存在
     */
    @Override
    public boolean exists(String key) {
        return RedisUtils.exists(key);
    }

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    @Override
    public void delete(String key) {
        RedisUtils.delete(key);
    }

    /**
     * 获取缓存值
     *
     * @param key 缓存键
     * @return 缓存值，如果不存在则返回 null
     */
    @Override
    public String get(String key) {
        return Convert.toStr(RedisUtils.get(key));
    }

    /**
     * 获取存储类型
     *
     * @return 返回小写的存储类型名称（redis）
     */
    @Override
    public String type() {
        return StorageType.REDIS.name().toLowerCase();
    }

    /**
     * 递增计数器
     *
     * @param key 缓存键
     * @param val 增加的值（目前实现忽略此参数，固定增加1）
     * @return 递增后的值
     */
    @Override
    public Long increment(String key, long val) {
        return RedisUtils.incr(key);
    }
}
