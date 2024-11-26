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



package com.ppxb.latte.starter.cache.redisson.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.ppxb.latte.starter.core.constant.StringConstants;
import org.redisson.api.*;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RedisUtils {

    private static final RedissonClient CLIENT = SpringUtil.getBean(RedissonClient.class);

    private RedisUtils() {
    }

    public static <T> void set(String key, T value) {
        CLIENT.getBucket(key).set(value);
    }

    public static <T> void set(String key, T value, Duration duration) {
        CLIENT.getBucket(key).set(value, duration);
    }

    public static <T> T get(String key) {
        RBucket<T> bucket = CLIENT.getBucket(key);
        return bucket.get();
    }

    public static <T> void setList(String key, List<T> value) {
        RList<T> list = CLIENT.getList(key);
        list.addAll(value);
    }

    public static <T> void setList(String key, List<T> value, Duration duration) {
        RBatch batch = CLIENT.createBatch();
        RListAsync<T> list = batch.getList(key);
        list.addAllAsync(value);
        list.expireAsync(duration);
        batch.execute();
    }

    public static <T> List<T> getList(String key) {
        RList<T> list = CLIENT.getList(key);
        return list.readAll();
    }

    public static boolean delete(String key) {
        return CLIENT.getBucket(key).delete();
    }

    public static void deleteByPattern(String pattern) {
        CLIENT.getKeys().deleteByPattern(pattern);
    }

    public static long incr(String key) {
        return CLIENT.getAtomicLong(key).incrementAndGet();
    }

    public static long decr(String key) {
        return CLIENT.getAtomicLong(key).decrementAndGet();
    }

    public static boolean expire(String key, Duration duration) {
        return CLIENT.getBucket(key).expire(duration);
    }

    public static long getTimeToLive(String key) {
        return CLIENT.getBucket(key).remainTimeToLive();
    }

    public static boolean exists(String key) {
        return CLIENT.getKeys().countExists(key) > 0;
    }

    public static Collection<String> keys(String pattern) {
        return CLIENT.getKeys().getKeysStreamByPattern(pattern).toList();
    }

    public static <T> boolean zAdd(String key, T value, double score) {
        RScoredSortedSet<T> zSet = CLIENT.getScoredSortedSet(key);
        return zSet.add(score, value);
    }

    public static <T> Double zScore(String key, T value) {
        RScoredSortedSet<T> zSet = CLIENT.getScoredSortedSet(key);
        return zSet.getScore(value);
    }

    public static <T> Integer zRank(String key, T value) {
        RScoredSortedSet<T> zSet = CLIENT.getScoredSortedSet(key);
        return zSet.rank(value);
    }

    public static <T> int zSize(String key) {
        RScoredSortedSet<T> zSet = CLIENT.getScoredSortedSet(key);
        return zSet.size();
    }

    public static <T> boolean zRemove(String key, T value) {
        RScoredSortedSet<T> zSet = CLIENT.getScoredSortedSet(key);
        return zSet.remove(value);
    }

    public static <T> int zRemoveRangeByScore(String key, double min, double max) {
        RScoredSortedSet<T> zSet = CLIENT.getScoredSortedSet(key);
        return zSet.removeRangeByScore(min, true, max, true);
    }

    public static <T> int zRemoveRangeByRank(String key, int startIndex, int endIndex) {
        RScoredSortedSet<T> zSet = CLIENT.getScoredSortedSet(key);
        return zSet.removeRangeByRank(startIndex, endIndex);
    }

    public static <T> Collection<T> zRangeByScore(String key, double min, double max) {
        RScoredSortedSet<T> zSet = CLIENT.getScoredSortedSet(key);
        return zSet.valueRange(min, true, max, true);
    }

    public static <T> Collection<T> zRangeByScore(String key, double min, double max, int offset, int count) {
        RScoredSortedSet<T> zSet = CLIENT.getScoredSortedSet(key);
        return zSet.valueRange(min, true, max, true, offset, count);
    }

    public static <T> int zCountRangeByScore(String key, double min, double max) {
        RScoredSortedSet<T> zSet = CLIENT.getScoredSortedSet(key);
        return zSet.count(min, true, max, true);
    }

    public static <T> double zSum(String key, Collection<T> values) {
        RScoredSortedSet<T> zSet = CLIENT.getScoredSortedSet(key);
        double sum = 0;
        for (T value : values) {
            Double score = zSet.getScore(value);
            if (null != score) {
                sum += score;
            }
        }
        return sum;
    }

    public static boolean rateLimit(String key, RateType rateType, int rate, int rateInterval) {
        RRateLimiter rateLimiter = CLIENT.getRateLimiter(key);
        rateLimiter.trySetRate(rateType, rate, Duration.ofSeconds(rateInterval));
        return rateLimiter.tryAcquire(1);
    }

    public static boolean tryLock(String key, long expire, long timeout) {
        return tryLock(key, expire, timeout, TimeUnit.MILLISECONDS);
    }

    public static boolean tryLock(String key, long expire, long timeout, TimeUnit unit) {
        RLock lock = getLock(key);
        try {
            return lock.tryLock(timeout, expire, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    public static boolean unlock(String key) {
        RLock lock = getLock(key);
        return unlock(lock);
    }

    public static boolean unlock(RLock lock) {
        if (lock.isHeldByCurrentThread()) {
            try {
                lock.unlockAsync().get();
                return true;
            } catch (ExecutionException | InterruptedException e) {
                return false;
            }
        }
        return false;
    }

    public static RLock getLock(String key) {
        return CLIENT.getLock(key);
    }

    public static String formatKey(String... subKeys) {
        return String.join(StringConstants.COLON, ArrayUtil.removeBlank(subKeys));
    }
}
