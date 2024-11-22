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



package com.ppxb.latte.starter.apidoc.autoconfigure;

import cn.hutool.core.map.MapUtil;
import com.ppxb.latte.starter.apidoc.handler.OpenAPIHandler;
import com.ppxb.latte.starter.core.autoconfigure.project.ProjectProperties;
import com.ppxb.latte.starter.core.util.GeneralPropertySourceFactory;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.SecurityService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * SpringDoc API文档自动配置类。
 * <p>
 * 该配置类提供以下功能：
 * <ul>
 * <li>配置API文档静态资源访问</li>
 * <li>创建和配置OpenAPI实例</li>
 * <li>自定义全局OpenAPI配置</li>
 * <li>配置安全方案</li>
 * </ul>
 *
 * @author ppxb
 * @see SpringDocConfiguration
 * @see OpenAPI
 * @since 1.0.0
 */
@EnableWebMvc
@AutoConfiguration(before = SpringDocConfiguration.class)
@EnableConfigurationProperties(SpringDocExtensionProperties.class)
@PropertySource(value = "classpath:default-api-doc.yml", factory = GeneralPropertySourceFactory.class)
public class SpringDocAutoConfiguration implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(SpringDocAutoConfiguration.class);

    /**
     * 配置静态资源处理。
     * <p>
     * 配置以下资源的访问路径：
     * <ul>
     * <li>favicon.ico - 网站图标</li>
     * <li>doc.html - API文档页面</li>
     * <li>webjars/** - API文档相关的静态资源，配置5小时缓存</li>
     * </ul>
     *
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/");
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/")
            .setCacheControl(CacheControl.maxAge(5, TimeUnit.HOURS).cachePublic());
    }

    /**
     * 创建OpenAPI实例。
     * <p>
     * 根据项目配置和扩展配置创建OpenAPI实例，包括：
     * <ul>
     * <li>配置API基本信息（标题、版本、描述等）</li>
     * <li>配置联系人信息</li>
     * <li>配置许可证信息</li>
     * <li>配置安全方案</li>
     * </ul>
     *
     * @param projectProperties            项目配置属性
     * @param springDocExtensionProperties SpringDoc扩展配置属性
     * @return 配置好的OpenAPI实例
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenAPI openAPI(ProjectProperties projectProperties,
                           SpringDocExtensionProperties springDocExtensionProperties) {
        OpenAPI openAPI = new OpenAPI();
        openAPI.info(buildInfo(projectProperties));

        Optional.ofNullable(springDocExtensionProperties.getComponents())
            .ifPresent(components -> configureComponents(openAPI, components));
        return openAPI;
    }

    private Info buildInfo(ProjectProperties projectProperties) {
        Info info = new Info().title(String.format("%s %s", projectProperties.getName(), "API 文档"))
            .version(projectProperties.getVersion())
            .description(projectProperties.getDescription());

        Optional.ofNullable(projectProperties.getContact())
            .ifPresent(contact -> info.contact(new Contact().name(contact.getName())
                .email(contact.getEmail())
                .url(contact.getUrl())));

        Optional.ofNullable(projectProperties.getLicense())
            .ifPresent(license -> info.license(new License().name(license.getName()).url(license.getUrl())));

        return info;
    }

    private void configureComponents(OpenAPI openAPI, Components components) {
        openAPI.components(components);

        Optional.ofNullable(components.getSecuritySchemes()).filter(MapUtil::isNotEmpty).ifPresent(schemes -> {
            SecurityRequirement requirement = new SecurityRequirement();
            schemes.values().stream().map(SecurityScheme::getName).forEach(requirement::addList);
            openAPI.addSecurityItem(requirement);
        });
    }

    /**
     * 创建全局OpenAPI自定义器。
     * <p>
     * 主要用于为所有API操作添加安全要求配置。
     * 如果配置了安全方案，会将其应用到所有API操作上。
     *
     * @param springDocExtensionProperties SpringDoc扩展配置属性
     * @return 全局OpenAPI自定义器
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer(SpringDocExtensionProperties springDocExtensionProperties) {
        return openApi -> Optional.ofNullable(openApi.getPaths())
            .ifPresent(paths -> paths.forEach((key, item) -> configureSecurityForPath(item, springDocExtensionProperties
                .getComponents())));
    }

    private void configureSecurityForPath(PathItem pathItem, Components components) {
        if (components == null || MapUtil.isEmpty(components.getSecuritySchemes())) {
            return;
        }

        SecurityRequirement requirement = new SecurityRequirement();
        components.getSecuritySchemes().values().stream().map(SecurityScheme::getName).forEach(requirement::addList);

        pathItem.readOperations().forEach(operation -> operation.addSecurityItem(requirement));
    }

    /**
     * 创建OpenAPIService实例。
     * <p>
     * 使用自定义的OpenAPIHandler处理API文档生成，支持：
     * <ul>
     * <li>安全配置</li>
     * <li>基础URL自定义</li>
     * <li>JavaDoc提供者</li>
     * </ul>
     *
     * @param openAPI                   OpenAPI实例
     * @param securityService           安全服务
     * @param springDocConfigProperties SpringDoc配置属性
     * @param propertyResolverUtils     属性解析工具
     * @param openApiBuilderCustomizers OpenAPI构建器自定义器列表
     * @param serverBaseUrlCustomizers  服务器基础URL自定义器列表
     * @param javadocProvider           JavaDoc提供者
     * @return OpenAPIService实例
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenAPIService openAPIBuilder(Optional<OpenAPI> openAPI,
                                         SecurityService securityService,
                                         SpringDocConfigProperties springDocConfigProperties,
                                         PropertyResolverUtils propertyResolverUtils,
                                         Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomizers,
                                         Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomizers,
                                         Optional<JavadocProvider> javadocProvider) {
        return new OpenAPIHandler(openAPI, securityService, springDocConfigProperties, propertyResolverUtils, openApiBuilderCustomizers, serverBaseUrlCustomizers, javadocProvider);
    }

    // TODO: ENUMS HANDLER

    @PostConstruct
    public void postConstruct() {
        log.debug("[Latte Starter] - Auto Configuration 'ApiDoc' completed initialization.");
    }
}
