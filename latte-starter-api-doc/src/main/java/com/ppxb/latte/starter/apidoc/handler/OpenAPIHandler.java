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

@SuppressWarnings("all")
public class OpenAPIHandler extends OpenAPIService {

    private static Class<?> basicErrorController;

    private final SecurityService securityService;

    private final Map<String, Object> mappingsMap = new HashMap<>();

    private final Map<HandlerMethod, Tag> springdocTags = new HashMap<>();

    private final Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomizers;

    private final Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomizers;

    private final SpringDocConfigProperties springDocConfigProperties;

    private final Map<String, OpenAPI> cachedOpenAPI = new HashMap<>();

    private final PropertyResolverUtils propertyResolverUtils;

    private final Optional<JavadocProvider> javadocProvider;

    private ApplicationContext context;

    private OpenAPI openAPI;

    private boolean isServersPresent;

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

    public static <E, T> Set<T> toSet(Collection<E> collection, Function<E, T> function) {
        if (CollUtil.isEmpty(collection) || function == null) {
            return CollUtil.newHashSet();
        }
        return collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.toSet());
    }

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
