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



package com.ppxb.latte.starter.auth.satoken.autoconfigure;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.ppxb.latte.starter.auth.satoken.autoconfigure.dao.SaTokenDaoConfiguration;
import com.ppxb.latte.starter.core.constant.PropertiesConstants;
import com.ppxb.latte.starter.core.constant.StringConstants;
import com.ppxb.latte.starter.core.util.GeneralPropertySourceFactory;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
@EnableConfigurationProperties(SaTokenExtensionProperties.class)
@ConditionalOnProperty(prefix = "sa-token.extension", name = PropertiesConstants.ENABLED, havingValue = "true")
@PropertySource(value = "classpath:default-auth-satoken.yml", factory = GeneralPropertySourceFactory.class)
public class SaTokenAutoConfiguration implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(SaTokenAutoConfiguration.class);

    private final SaTokenExtensionProperties properties;

    public SaTokenAutoConfiguration(SaTokenExtensionProperties properties) {
        this.properties = properties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(SpringUtil.getBean(SaInterceptor.class)).addPathPatterns(StringConstants.PATH_PATTERN);
    }

    @Bean
    @ConditionalOnMissingBean
    public SaInterceptor saInterceptor() {
        return new SaInterceptor(handle -> SaRouter.match(StringConstants.PATH_PATTERN)
            .notMatch(properties.getSecurity().getExcludes())
            .check(r -> StpUtil.checkLogin()));
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sa-token.extension", name = "enableJwt", havingValue = "true")
    public StpLogic stpLogic() {
        return new StpLogicJwtForSimple();
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Latte Starter] - Auto Configuration 'SaToken' completed initialization.");
    }

    @Configuration
    @Import({SaTokenDaoConfiguration.Default.class, SaTokenDaoConfiguration.Redis.class,
        SaTokenDaoConfiguration.Custom.class})
    protected static class SaTokenDaoAutoConfiguration {
    }
}
