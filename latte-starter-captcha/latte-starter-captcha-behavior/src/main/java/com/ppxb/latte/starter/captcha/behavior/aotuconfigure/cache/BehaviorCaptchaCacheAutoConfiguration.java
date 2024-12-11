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

import com.anji.captcha.service.CaptchaCacheService;
import com.anji.captcha.service.impl.CaptchaCacheServiceMemImpl;
import com.anji.captcha.service.impl.CaptchaServiceFactory;
import com.ppxb.latte.starter.cache.redisson.autoconfigure.RedissonAutoConfiguration;
import com.ppxb.latte.starter.captcha.behavior.enums.StorageType;
import com.ppxb.latte.starter.core.constant.PropertiesConstants;
import jakarta.annotation.PostConstruct;
import org.redisson.client.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;

/**
 * 行为验证码缓存自动配置类
 *
 * @author ppxb
 * @since 1.0.0
 */
@AutoConfiguration
public class BehaviorCaptchaCacheAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(BehaviorCaptchaCacheAutoConfiguration.class);

    private BehaviorCaptchaCacheAutoConfiguration() {
    }

    /**
     * 默认缓存实现（内存）
     */
    @AutoConfiguration
    @ConditionalOnMissingBean(CaptchaCacheService.class)
    @ConditionalOnProperty(name = PropertiesConstants.CAPTCHA_BEHAVIOR + ".cache-type", havingValue = "default", matchIfMissing = true)
    public static class Default {

        @Bean
        public CaptchaCacheService captchaCacheService() {
            return new CaptchaCacheServiceMemImpl();
        }

        @PostConstruct
        public void postConstruct() {
            CaptchaServiceFactory.cacheService.put(StorageType.DEFAULT.name().toLowerCase(), captchaCacheService());
            log.debug("[Latte Starter] - Auto Configuration 'Captcha-Behavior-Cache-Default' completed initialization.");
        }
    }

    /**
     * Redis 缓存实现
     */
    @AutoConfiguration(before = RedissonAutoConfiguration.class)
    @ConditionalOnClass(RedisClient.class)
    @ConditionalOnMissingBean(CaptchaCacheService.class)
    @ConditionalOnProperty(name = PropertiesConstants.CAPTCHA_BEHAVIOR + ".cache-type", havingValue = "redis")
    public static class Redis {

        @Bean
        public CaptchaCacheService captchaCacheService() {
            return new BehaviorCaptchaCacheServiceImpl();
        }

        @PostConstruct
        public void postConstruct() {
            CaptchaServiceFactory.cacheService.put(StorageType.REDIS.name().toLowerCase(), captchaCacheService());
            log.debug("[Latte Starter] - Auto Configuration 'Captcha-Behavior-Cache-Redis' completed initialization.");
        }
    }

    /**
     * 自定义缓存实现
     */
    @AutoConfiguration
    @ConditionalOnProperty(name = PropertiesConstants.CAPTCHA_BEHAVIOR + ".cache-type", havingValue = "custom")
    public static class Custom {

        @Bean
        @ConditionalOnMissingBean
        public CaptchaCacheService captchaCacheService() {
            if (log.isDebugEnabled()) {
                log.error("Consider defining a bean of type '{}' in your configuration.", ResolvableType
                    .forClass(CaptchaCacheService.class));
            }
            throw new NoSuchBeanDefinitionException(CaptchaCacheService.class);
        }

        @PostConstruct
        public void postConstruct() {
            CaptchaServiceFactory.cacheService.put(StorageType.CUSTOM.name().toLowerCase(), captchaCacheService());
            log.debug("[Latte Starter] - Auto Configuration 'Captcha-Behavior-Cache-Custom' completed initialization.");
        }
    }
}
