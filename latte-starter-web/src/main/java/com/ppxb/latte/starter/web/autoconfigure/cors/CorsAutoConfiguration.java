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


package com.ppxb.latte.starter.web.autoconfigure.cors;

import com.ppxb.latte.starter.core.constant.PropertiesConstants;
import com.ppxb.latte.starter.core.constant.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS (跨域资源共享) 自动配置类。
 * <p>
 * 该配置类为Spring Web应用程序提供自动的CORS过滤配置。
 * 满足以下条件时激活：
 * <ul>
 * <li>应用程序是Web应用</li>
 * <li>配置属性'latte.web.cors.enabled'设置为'true'</li>
 * <li>容器中没有其他{@link CorsFilter}类型的Bean</li>
 * </ul>
 * <p>
 * 配置支持通配符(*)和特定源站配置。
 * 使用通配符时，出于安全考虑会自动禁用凭证支持。
 *
 * @author ppxb
 * @see CorsProperties
 * @see CorsFilter
 * @since 1.0.0
 */
@Lazy
@AutoConfiguration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = PropertiesConstants.WEB_CORS, name = PropertiesConstants.ENABLED, havingValue = "true")
@EnableConfigurationProperties(CorsProperties.class)
public class CorsAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CorsAutoConfiguration.class);

    /**
     * 预检请求响应的缓存时间（秒）。
     * 设置为30分钟（1800秒）以减少预检请求次数。
     */
    private static final long MAX_AGE = 1800L;

    /**
     * 根据提供的配置属性创建和配置 {@link CorsFilter} Bean。
     * <p>
     * 该方法配置CORS行为的逻辑如下：
     * <ul>
     * <li>设置预检响应缓存时间为30分钟</li>
     * <li>如果允许的源站包含通配符，则配置通配符源站模式</li>
     * <li>仅在配置特定源站时启用凭证支持</li>
     * <li>应用配置的允许方法、请求头和暴露的响应头</li>
     * </ul>
     *
     * @param properties CORS配置属性
     * @return 配置好的 {@link CorsFilter} Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public CorsFilter corsFilter(CorsProperties properties) {
        CorsConfiguration config = new CorsConfiguration();
        config.setMaxAge(MAX_AGE);
        if (properties.getAllowedOrigins().contains(StringConstants.ASTERISK)) {
            config.addAllowedOriginPattern(StringConstants.ASTERISK);
        } else {
            config.setAllowCredentials(true);
            properties.getAllowedOrigins().forEach(config::addAllowedOrigin);
        }

        properties.getAllowedMethods().forEach(config::addAllowedMethod);
        properties.getAllowedHeaders().forEach(config::addAllowedHeader);
        properties.getExposedHeaders().forEach(config::addExposedHeader);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(StringConstants.PATH_PATTERN, config);

        log.debug("[Latte Starter] - Auto Configuration 'Web-CorsFilter' completed initialization.");
        return new CorsFilter(source);
    }
}
