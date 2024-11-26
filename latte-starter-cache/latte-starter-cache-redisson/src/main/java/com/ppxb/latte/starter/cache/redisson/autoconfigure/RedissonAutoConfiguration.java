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



package com.ppxb.latte.starter.cache.redisson.autoconfigure;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppxb.latte.starter.core.constant.PropertiesConstants;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "spring.data.redisson", value = PropertiesConstants.ENABLED, havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RedissonAutoConfiguration.class);

    private static final String REDIS_PROTOCOL_PREFIX = "redis://";

    private static final String REDISS_PROTOCOL_PREFIX = "rediss://";

    private final RedissonProperties properties;

    private final RedisProperties redisProperties;

    private final ObjectMapper objectMapper;

    public RedissonAutoConfiguration(RedissonProperties properties,
                                     RedisProperties redisProperties,
                                     ObjectMapper objectMapper) {
        this.properties = properties;
        this.redisProperties = redisProperties;
        this.objectMapper = objectMapper;
    }

    @Bean
    public RedissonAutoConfigurationCustomizer redissonAutoConfigurationCustomizer() {
        return config -> {
            RedissonProperties.Mode mode = properties.getMode();
            String protocolPrefix = redisProperties.getSsl().isEnabled()
                ? REDIS_PROTOCOL_PREFIX
                : REDISS_PROTOCOL_PREFIX;
            switch (mode) {
                case CLUSTER -> this.buildClusterModeConfig(config, protocolPrefix);
                case SENTINEL -> this.buildSentinelModeConfig(config, protocolPrefix);
                default -> this.buildSingleModeConfig(config, protocolPrefix);
            }

            // Jackson 处理
            config.setCodec(new JsonJacksonCodec(objectMapper));
            log.debug("[Latte Starter] - Auto Configuration 'Redisson' completed initialization.");
        };
    }

    private void buildClusterModeConfig(Config config, String protocolPrefix) {
        ClusterServersConfig clusterConfig = config.useClusterServers();
        ClusterServersConfig customConfig = properties.getClusterServersConfig();

        if (null != customConfig) {
            BeanUtil.copyProperties(customConfig, clusterConfig);
            clusterConfig.setNodeAddresses(customConfig.getNodeAddresses());
        }
        // 下方配置如果为空，则使用 Redis 的配置
        if (CollUtil.isEmpty(clusterConfig.getNodeAddresses())) {
            redisProperties.getCluster()
                .getNodes()
                .stream()
                .map(node -> protocolPrefix + node)
                .forEach(clusterConfig::addNodeAddress);
        }

        if (CharSequenceUtil.isBlank(clusterConfig.getPassword())) {
            clusterConfig.setPassword(redisProperties.getPassword());
        }
    }

    private void buildSentinelModeConfig(Config config, String protocolPrefix) {
        SentinelServersConfig sentinelConfig = config.useSentinelServers();
        SentinelServersConfig customConfig = properties.getSentinelServersConfig();

        if (null != customConfig) {
            BeanUtil.copyProperties(customConfig, sentinelConfig);
            sentinelConfig.setSentinelAddresses(customConfig.getSentinelAddresses());
        }
        // 下方配置如果为空，则使用 Redis 的配置
        if (CollUtil.isEmpty(sentinelConfig.getSentinelAddresses())) {
            redisProperties.getSentinel()
                .getNodes()
                .stream()
                .map(node -> protocolPrefix + node)
                .forEach(sentinelConfig::addSentinelAddress);
        }

        if (CharSequenceUtil.isBlank(sentinelConfig.getPassword())) {
            sentinelConfig.setPassword(redisProperties.getPassword());
        }

        if (CharSequenceUtil.isBlank(sentinelConfig.getMasterName())) {
            sentinelConfig.setMasterName(redisProperties.getSentinel().getMaster());
        }
    }

    private void buildSingleModeConfig(Config config, String protocolPrefix) {
        SingleServerConfig singleConfig = config.useSingleServer();
        SingleServerConfig customConfig = properties.getSingleServerConfig();

        if (null != customConfig) {
            BeanUtil.copyProperties(customConfig, singleConfig);
        }
        // 下方配置如果为空，则使用 Redis 的配置
        singleConfig.setDatabase(redisProperties.getDatabase());

        if (CharSequenceUtil.isBlank(singleConfig.getPassword())) {
            singleConfig.setPassword(redisProperties.getPassword());
        }

        if (CharSequenceUtil.isBlank(singleConfig.getAddress())) {
            String address = String.format("%s%s:%d", protocolPrefix, redisProperties.getHost(), redisProperties
                .getPort());
            singleConfig.setAddress(address);
        }
    }
}
