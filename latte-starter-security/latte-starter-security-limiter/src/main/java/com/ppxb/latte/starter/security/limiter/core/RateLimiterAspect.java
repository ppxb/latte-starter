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



package com.ppxb.latte.starter.security.limiter.core;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.ppxb.latte.starter.cache.redisson.util.RedisUtils;
import com.ppxb.latte.starter.core.constant.StringConstants;
import com.ppxb.latte.starter.core.util.expression.ExpressionUtils;
import com.ppxb.latte.starter.security.limiter.annotation.RateLimiter;
import com.ppxb.latte.starter.security.limiter.annotation.RateLimiters;
import com.ppxb.latte.starter.security.limiter.autoconfigure.RateLimiterProperties;
import com.ppxb.latte.starter.security.limiter.enums.LimitType;
import com.ppxb.latte.starter.security.limiter.exception.RateLimiterException;
import com.ppxb.latte.starter.web.util.SpringWebUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RateLimiterAspect {

    private static final ConcurrentHashMap<String, RRateLimiter> RATE_LIMITER_CACHE = new ConcurrentHashMap<>();

    private final RateLimiterProperties properties;

    private final RateLimiterNameGenerator nameGenerator;

    private final RedissonClient redissonClient;

    public RateLimiterAspect(RateLimiterProperties properties,
                             RateLimiterNameGenerator nameGenerator,
                             RedissonClient redissonClient) {
        this.properties = properties;
        this.nameGenerator = nameGenerator;
        this.redissonClient = redissonClient;
    }

    @Pointcut("@annotation(com.ppxb.latte.starter.security.limiter.annotation.RateLimiter)")
    public void rateLimiterPointcut() {
    }

    @Pointcut("@annotation(com.ppxb.latte.starter.security.limiter.annotation.RateLimiters)")
    public void rateLimitersPointcut() {
    }

    @Around("@annotation(rateLimiter)")
    public Object aroundRateLimiter(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) throws Throwable {
        if (isRateLimited(joinPoint, rateLimiter)) {
            throw new RateLimiterException(rateLimiter.message());
        }
        return joinPoint.proceed();
    }

    @Around("@annotation(rateLimiters)")
    public Object aroundRateLimiters(ProceedingJoinPoint joinPoint, RateLimiters rateLimiters) throws Throwable {
        for (RateLimiter rateLimiter : rateLimiters.value()) {
            if (isRateLimited(joinPoint, rateLimiter)) {
                throw new RateLimiterException(rateLimiter.message());
            }
        }
        return joinPoint.proceed();
    }

    private boolean isRateLimited(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) {
        try {
            String cacheKey = getCacheKey(joinPoint, rateLimiter);
            RRateLimiter rRateLimiter = RATE_LIMITER_CACHE.computeIfAbsent(cacheKey, key -> redissonClient
                .getRateLimiter(cacheKey));
            RateType rateType = rateLimiter.type() == LimitType.CLUSTER ? RateType.PER_CLIENT : RateType.OVERALL;
            int rate = rateLimiter.rate();
            int rateInterval = rateLimiter.interval();
            RateIntervalUnit rateIntervalUnit = rateLimiter.unit();
            if (isConfigurationUpdateNeeded(rRateLimiter, rateType, rate, rateInterval, rateIntervalUnit)) {
                rRateLimiter.setRate(rateType, rate, rateInterval, rateIntervalUnit);
            }
            return !rRateLimiter.tryAcquire();
        } catch (Exception e) {
            throw new RateLimiterException("服务器限流异常，请稍候再试", e);
        }
    }

    private String getCacheKey(JoinPoint joinPoint, RateLimiter rateLimiter) {
        Object target = joinPoint.getTarget();
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        String name = rateLimiter.name();
        if (CharSequenceUtil.isBlank(name)) {
            name = nameGenerator.generate(target, method, args);
        }
        String key = rateLimiter.key();
        if (CharSequenceUtil.isNotBlank(key)) {
            Object eval = ExpressionUtils.eval(key, target, method, args);
            if (ObjectUtil.isNull(eval)) {
                throw new RateLimiterException("限流 Key 解析错误");
            }
            key = Convert.toStr(eval);
        }
        String suffix = switch (rateLimiter.type()) {
            case IP -> JakartaServletUtil.getClientIP(SpringWebUtils.getRequest());
            case CLUSTER -> redissonClient.getId();
            default -> StringConstants.EMPTY;
        };
        return RedisUtils.formatKey(properties.getKeyPrefix(), name, key, suffix);
    }

    private boolean isConfigurationUpdateNeeded(RRateLimiter rRateLimiter,
                                                RateType rateType,
                                                long rate,
                                                long rateInterval,
                                                RateIntervalUnit rateIntervalUnit) {
        RateLimiterConfig config = rRateLimiter.getConfig();
        return !Objects.equals(config.getRateType(), rateType) || !Objects.equals(config.getRate(), rate) || !Objects
            .equals(config.getRateInterval(), rateIntervalUnit.toMillis(rateInterval));
    }
}
