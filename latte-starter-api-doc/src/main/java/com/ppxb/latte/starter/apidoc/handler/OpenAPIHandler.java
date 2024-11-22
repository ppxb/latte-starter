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



package com.ppxb.latte.starter.apidoc.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import io.swagger.v3.core.jackson.TypeNameResolver;
import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tags;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.SecurityService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * OpenAPI文档处理器，负责构建和管理API文档的标签、安全配置等信息。
 * 该类继承自{@link OpenAPIService}，提供了更丰富的文档处理功能。
 *
 * <p>主要功能：
 * <ul>
 * <li>处理API文档的标签(Tags)生成</li>
 * <li>管理安全配置要求</li>
 * <li>集成JavaDoc文档</li>
 * <li>支持自定义API构建</li>
 * </ul>
 *
 * @author ppxb
 * @since 1.0.0
 */
@SuppressWarnings("all")
public class OpenAPIHandler extends OpenAPIService {

    /**
     * 基础错误控制器类
     */
    private static Class<?> basicErrorController;

    /**
     * 安全服务，用于处理API安全相关配置
     */
    private final SecurityService securityService;

    /**
     * 映射关系缓存
     */
    private final Map<String, Object> mappingsMap = new HashMap<>();

    /**
     * SpringDoc标签缓存
     */
    private final Map<HandlerMethod, Tag> springdocTags = new HashMap<>();

    /**
     * OpenAPI构建器自定义器列表
     */
    private final Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomizers;

    /**
     * 服务器基础URL自定义器列表
     */
    private final Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomizers;

    /**
     * SpringDoc配置属性
     */
    private final SpringDocConfigProperties springDocConfigProperties;

    /**
     * OpenAPI实例缓存
     */
    private final Map<String, OpenAPI> cachedOpenAPI = new HashMap<>();

    /**
     * 属性解析工具
     */
    private final PropertyResolverUtils propertyResolverUtils;

    /**
     * JavaDoc提供者
     */
    private final Optional<JavadocProvider> javadocProvider;

    /**
     * Spring应用上下文
     */
    private ApplicationContext context;

    /**
     * OpenAPI实例
     */
    private OpenAPI openAPI;

    /**
     * 服务器配置是否存在标志
     */
    private boolean isServersPresent;

    /**
     * 服务器基础URL
     */
    private String serverBaseUrl;

    public OpenAPIHandler(Optional<OpenAPI> openAPI,
                          SecurityService securityService,
                          SpringDocConfigProperties springDocConfigProperties,
                          PropertyResolverUtils propertyResolverUtils,
                          Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomizers,
                          Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomizers,
                          Optional<JavadocProvider> javadocProvider) {
        super(openAPI, securityService, springDocConfigProperties, propertyResolverUtils, openApiBuilderCustomizers, serverBaseUrlCustomizers, javadocProvider);
        if (openAPI.isPresent()) {
            this.openAPI = openAPI.get();
            if (null == this.openAPI.getComponents()) {
                this.openAPI.setComponents(new Components());
            }
            if (null == this.openAPI.getPaths()) {
                this.openAPI.setPaths(new Paths());
            }
            if (!CollectionUtils.isEmpty(this.openAPI.getServers())) {
                this.isServersPresent = true;
            }
        }
        this.propertyResolverUtils = propertyResolverUtils;
        this.securityService = securityService;
        this.springDocConfigProperties = springDocConfigProperties;
        this.openApiBuilderCustomizers = openApiBuilderCustomizers;
        this.serverBaseUrlCustomizers = serverBaseUrlCustomizers;
        this.javadocProvider = javadocProvider;
        if (springDocConfigProperties.isUseFqn()) {
            TypeNameResolver.std.setUseFqn(true);
        }
    }

    /**
     * 将集合转换为Set集合的工具方法。
     *
     * @param collection 源集合
     * @param function   转换函数
     * @param <E>        源集合元素类型
     * @param <T>        目标集合元素类型
     * @return 转换后的Set集合
     */
    public static <E, T> Set<T> toSet(Collection<E> collection, Function<E, T> function) {
        if (CollUtil.isEmpty(collection) || function == null) {
            return CollUtil.newHashSet();
        }
        return collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * 构建API操作的Tag信息。
     *
     * <p>该方法处理以下标签来源：
     * <ul>
     * <li>方法级别的Tag注解</li>
     * <li>类级别的Tag注解</li>
     * <li>JavaDoc文档</li>
     * <li>自动生成的Tag</li>
     * </ul>
     *
     * @param handlerMethod 处理器方法
     * @param operation     API操作
     * @param openAPI       OpenAPI实例
     * @param locale        区域设置
     * @return 更新后的API操作
     */
    @Override
    public Operation buildTags(HandlerMethod handlerMethod, Operation operation, OpenAPI openAPI, Locale locale) {
        Set<Tag> tags = new HashSet<>();
        Set<String> tagsStr = new HashSet<>();
        buildTagsFromMethod(handlerMethod.getMethod(), tags, tagsStr, locale);
        buildTagsFromClass(handlerMethod.getBeanType(), tags, tagsStr, locale);

        if (!CollectionUtils.isEmpty(tagsStr)) {
            tagsStr = tagsStr.stream().map(s -> propertyResolverUtils.resolve(s, locale)).collect(Collectors.toSet());
        }

        if (springdocTags.containsKey(handlerMethod)) {
            Tag tag = springdocTags.get(handlerMethod);
            tagsStr.add(tag.getName());
            if (null == openAPI.getTags() || !openAPI.getTags().contains(tag)) {
                openAPI.addTagsItem(tag);
            }
        }

        if (!CollectionUtils.isEmpty(tagsStr)) {
            if (CollectionUtils.isEmpty(operation.getTags())) {
                operation.setTags(new ArrayList<>(tagsStr));
            } else {
                Set<String> operationTagsSet = new HashSet<>(operation.getTags());
                operationTagsSet.addAll(tagsStr);
                operation.getTags().clear();
                operation.getTags().addAll(operationTagsSet);
            }
        }

        if (isAutoTagClasses(operation)) {
            if (javadocProvider.isPresent()) {
                String description = javadocProvider.get().getClassJavadoc(handlerMethod.getBeanType());
                if (StringUtils.isNotBlank(description)) {
                    Tag tag = new Tag();
                    List<String> list = IoUtil.readLines(new StringReader(description), new ArrayList<>());
                    operation.addTagsItem(list.getFirst());
                    tag.setName(list.getFirst());
                    tag.setDescription(description);

                    if (null == openAPI.getTags() || !openAPI.getTags().contains(tag)) {
                        openAPI.addTagsItem(tag);
                    }
                }
            } else {
                String tagAutoName = splitCamelCase(handlerMethod.getBeanType().getSimpleName());
                operation.addTagsItem(tagAutoName);
            }
        }

        if (!CollectionUtils.isEmpty(tags)) {
            List<Tag> openAPITags = openAPI.getTags();
            if (!CollectionUtils.isEmpty(openAPITags)) {
                tags.addAll(openAPITags);
            }
            openAPI.setTags(new ArrayList<>(tags));
        }

        SecurityRequirement[] securityRequirements = securityService.getSecurityRequirements(handlerMethod);
        if (null != securityRequirements) {
            if (securityRequirements.length == 0) {
                operation.setSecurity(Collections.emptyList());
            } else {
                securityService.buildSecurityRequirement(securityRequirements, operation);
            }
        }
        return operation;
    }

    /**
     * 从方法注解中构建Tag信息。
     *
     * @param method  方法对象
     * @param tags    Tag集合
     * @param tagsStr Tag字符串集合
     * @param locale  区域设置
     */
    private void buildTagsFromMethod(Method method, Set<Tag> tags, Set<String> tagsStr, Locale locale) {
        Set<Tags> tagsSet = AnnotatedElementUtils.findAllMergedAnnotations(method, Tags.class);
        Set<io.swagger.v3.oas.annotations.tags.Tag> methodTags = tagsSet.stream()
            .flatMap(tag -> Stream.of(tag.value()))
            .collect(Collectors.toSet());
        methodTags.addAll(AnnotatedElementUtils
            .findAllMergedAnnotations(method, io.swagger.v3.oas.annotations.tags.Tag.class));
        if (!CollectionUtils.isEmpty(methodTags)) {
            tagsStr.addAll(toSet(methodTags, tag -> propertyResolverUtils.resolve(tag.name(), locale)));
            List<io.swagger.v3.oas.annotations.tags.Tag> allTags = new ArrayList<>(methodTags);
            addTags(allTags, tags, locale);
        }

    }

    /**
     * 添加Tag到Tag集合中。
     *
     * <p>该方法会：
     * <ul>
     * <li>解析Tag名称和描述</li>
     * <li>处理国际化</li>
     * <li>确保Tag唯一性</li>
     * </ul>
     *
     * @param sourceTags 源Tag列表
     * @param tags       目标Tag集合
     * @param locale     区域设置
     */
    private void addTags(List<io.swagger.v3.oas.annotations.tags.Tag> sourceTags, Set<Tag> tags, Locale locale) {
        Optional<Set<Tag>> optionalTagSet = AnnotationsUtils.getTags(sourceTags
            .toArray(new io.swagger.v3.oas.annotations.tags.Tag[0]), true);
        optionalTagSet.ifPresent(tagsSet -> {
            tagsSet.forEach(tag -> {
                tag.name(propertyResolverUtils.resolve(tag.getName(), locale));
                tag.description(propertyResolverUtils.resolve(tag.getDescription(), locale));
                if (tags.stream().noneMatch(t -> t.getName().equals(tag.getName()))) {
                    tags.add(tag);
                }
            });
        });
    }
}
