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



package com.ppxb.latte.starter.auth.justauth.autoconfigure;

import com.ppxb.latte.starter.auth.justauth.core.AuthStateCacheRedisDefaultImpl;
import com.ppxb.latte.starter.core.constant.PropertiesConstants;
import jakarta.annotation.PostConstruct;
import me.zhyd.oauth.cache.AuthStateCache;
import org.redisson.client.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(before = com.xkcoding.justauth.autoconfigure.JustAuthAutoConfiguration.class)
@ConditionalOnProperty(prefix = "justauth", name = PropertiesConstants.ENABLED, havingValue = "true", matchIfMissing = true)
public class JustAuthAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JustAuthAutoConfiguration.class);

    @Bean
    @ConditionalOnClass(RedisClient.class)
    @ConditionalOnProperty(prefix = "justauth.cache", name = "type", havingValue = "redis")
    public AuthStateCache authStateCache() {
        AuthStateCacheRedisDefaultImpl impl = new AuthStateCacheRedisDefaultImpl();
        log.debug("[Latte Starter] - Auto Configuration 'JustAuth-AuthStateCache-Redis' completed initialization.");
        return impl;
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Latte Starter] - Auto Configuration 'JustAuth' completed initialization.");
    }
}
