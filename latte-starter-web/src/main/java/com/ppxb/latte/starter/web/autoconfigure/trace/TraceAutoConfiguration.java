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



package com.ppxb.latte.starter.web.autoconfigure.trace;

import com.ppxb.latte.starter.core.constant.PropertiesConstants;
import com.yomahub.tlog.id.TLogIdGenerator;
import com.yomahub.tlog.id.TLogIdGeneratorLoader;
import com.yomahub.tlog.spring.TLogPropertyInit;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;

@AutoConfiguration
@EnableConfigurationProperties(TraceProperties.class)
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = PropertiesConstants.WEB_TRACE, name = PropertiesConstants.ENABLED, havingValue = "true")
public class TraceAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(TraceAutoConfiguration.class);

    private final TraceProperties traceProperties;

    public TraceAutoConfiguration(TraceProperties traceProperties) {
        this.traceProperties = traceProperties;
    }

    @Bean
    @Primary
    public TLogPropertyInit tLogPropertyInit(TLogIdGenerator tLogIdGenerator) {
        TLogProperties properties = traceProperties.getTlog();
        TLogPropertyInit propertyInit = new TLogPropertyInit();
        propertyInit.setPattern(properties.getPattern());
        propertyInit.setEnableInvokeTimePrint(properties.getEnableInvokeTimePrint());
        propertyInit.setMdcEnable(properties.getMdcEnable());
        TLogIdGeneratorLoader.setIdGenerator(tLogIdGenerator);
        return propertyInit;
    }

    /**
     * TLog 过滤器配置
     */
    @Bean
    public FilterRegistrationBean<TLogServletFilter> tLogServletFilter() {
        FilterRegistrationBean<TLogServletFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TLogServletFilter(traceProperties));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    /**
     * 自定义 Trace ID 生成器
     */
    @Bean
    @ConditionalOnMissingBean
    public TLogIdGenerator tLogIdGenerator() {
        return new TraceIdGenerator();
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Latte Starter] - Auto Configuration 'Web-Trace' completed initialization.");
    }
}
