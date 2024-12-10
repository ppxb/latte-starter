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



package com.ppxb.latte.starter.extension.tenant.autoconfigure;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.ppxb.latte.starter.core.constant.PropertiesConstants;
import com.ppxb.latte.starter.extension.tenant.config.TenantDataSourceProvider;
import com.ppxb.latte.starter.extension.tenant.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ResolvableType;

@AutoConfiguration
@EnableConfigurationProperties(TenantProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.TENANT, name = PropertiesConstants.ENABLED, havingValue = "true", matchIfMissing = true)
public class TenantAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(TenantAutoConfiguration.class);

    private TenantAutoConfiguration() {
    }

    @AutoConfiguration
    @ConditionalOnProperty(name = PropertiesConstants.TENANT + ".isolation-level", havingValue = "true", matchIfMissing = true)
    public static class Line {
        static {
            log.debug("[Latte Starter] - Auto Configuration 'Tenant-Line' completed initialization.");
        }

        @Bean
        @ConditionalOnMissingBean
        public TenantLineInnerInterceptor tenantLineInnerInterceptor(TenantLineHandler tenantLineHandler) {
            return new TenantLineInnerInterceptor(tenantLineHandler);
        }

        @Bean
        @ConditionalOnMissingBean
        public TenantLineHandler tenantLineHandler(TenantProperties tenantProperties) {
            return new DefaultTenantLineHandler(tenantProperties);
        }
    }

    /**
     * 租户隔离级别：数据源级
     */
    @AutoConfiguration
    @ConditionalOnProperty(name = PropertiesConstants.TENANT + ".isolation-level", havingValue = "datasource")
    public static class DataSource {
        static {
            log.debug("[Latte Starter] - Auto Configuration 'Tenant-DataSource' completed initialization.");
        }

        /**
         * 租户数据源级隔离通知
         */
        @Bean
        @ConditionalOnMissingBean
        public TenantDataSourceAdvisor tenantDataSourceAdvisor(TenantDataSourceInterceptor tenantDataSourceInterceptor) {
            return new TenantDataSourceAdvisor(tenantDataSourceInterceptor);
        }

        /**
         * 租户数据源级隔离拦截器
         */
        @Bean
        @ConditionalOnMissingBean
        public TenantDataSourceInterceptor tenantDataSourceInterceptor(TenantDataSourceHandler tenantDataSourceHandler) {
            return new TenantDataSourceInterceptor(tenantDataSourceHandler);
        }

        /**
         * 租户数据源级隔离处理器（默认）
         */
        @Bean
        @ConditionalOnMissingBean
        public TenantDataSourceHandler tenantDataSourceHandler(TenantDataSourceProvider tenantDataSourceProvider,
                                                               DynamicRoutingDataSource dynamicRoutingDataSource,
                                                               DefaultDataSourceCreator dataSourceCreator) {
            return new DefaultTenantDataSourceHandler(tenantDataSourceProvider, dynamicRoutingDataSource, dataSourceCreator);
        }

        /**
         * 多租户数据源提供者
         */
        @Bean
        @ConditionalOnMissingBean
        public TenantDataSourceProvider tenantDataSourceProvider() {
            if (log.isErrorEnabled()) {
                log.error("Consider defining a bean of type '{}' in your configuration.", ResolvableType
                    .forClass(TenantDataSourceProvider.class));
            }
            throw new NoSuchBeanDefinitionException(TenantDataSourceProvider.class);
        }
    }
}
