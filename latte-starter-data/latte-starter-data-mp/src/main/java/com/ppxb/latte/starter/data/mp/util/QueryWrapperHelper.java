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



package com.ppxb.latte.starter.data.mp.util;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ppxb.latte.starter.core.exception.BadRequestException;
import com.ppxb.latte.starter.core.util.ReflectUtils;
import com.ppxb.latte.starter.core.validation.ValidationUtils;
import com.ppxb.latte.starter.data.core.annotation.Query;
import com.ppxb.latte.starter.data.core.annotation.QueryIgnore;
import com.ppxb.latte.starter.data.core.enums.QueryType;
import com.ppxb.latte.starter.data.core.util.SqlInjectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class QueryWrapperHelper {

    private static final Logger log = LoggerFactory.getLogger(QueryWrapperHelper.class);

    private QueryWrapperHelper() {
    }

    public static <Q, R> QueryWrapper<R> build(Q query) {
        return build(query, Sort.unsorted());
    }

    public static <Q, R> QueryWrapper<R> build(Q query, Sort sort) {
        QueryWrapper<R> queryWrapper = new QueryWrapper<>();
        if (query == null) {
            return queryWrapper;
        }
        if (sort != null && sort.isSorted()) {
            for (Sort.Order order : sort) {
                String field = CharSequenceUtil.toUnderlineCase(order.getProperty());
                ValidationUtils.throwIf(SqlInjectionUtils.check(field), "排序字段包含非法字符");
                queryWrapper.orderBy(true, order.isAscending(), field);
            }
        }
        List<Field> fieldList = ReflectUtils.getNonStaticFields(query.getClass());
        return build(query, fieldList, queryWrapper);
    }

    public static <Q, R> QueryWrapper<R> build(Q query, List<Field> fieldList, QueryWrapper<R> queryWrapper) {
        if (query == null) {
            return queryWrapper;
        }
        for (Field field : fieldList) {
            List<Consumer<QueryWrapper<R>>> consumers = buildWrapperConsumer(query, field);
            queryWrapper.and(CollUtil.isNotEmpty(consumers), q -> consumers.forEach(q::or));
        }
        return queryWrapper;
    }

    private static <Q, R> List<Consumer<QueryWrapper<R>>> buildWrapperConsumer(Q query, Field field) {
        try {
            // 如果字段值为空，直接返回
            Object fieldValue = ReflectUtil.getFieldValue(query, field);
            if (ObjectUtil.isEmpty(fieldValue)) {
                return Collections.emptyList();
            }
            // 设置了 @QueryIgnore 注解，直接忽略
            QueryIgnore queryIgnoreAnnotation = AnnotationUtil.getAnnotation(field, QueryIgnore.class);
            if (null != queryIgnoreAnnotation) {
                return Collections.emptyList();
            }
            // 建议：数据库表列建议采用下划线连接法命名，程序变量建议采用驼峰法命名
            String fieldName = ReflectUtil.getFieldName(field);
            // 没有 @Query 注解，默认等值查询
            Query queryAnnotation = AnnotationUtil.getAnnotation(field, Query.class);
            if (null == queryAnnotation) {
                return Collections.singletonList(q -> q.eq(CharSequenceUtil.toUnderlineCase(fieldName), fieldValue));
            }
            // 解析单列查询
            QueryType queryType = queryAnnotation.type();
            String[] columns = queryAnnotation.columns();
            final int columnLength = ArrayUtil.length(columns);
            List<Consumer<QueryWrapper<R>>> consumers = new ArrayList<>(columnLength);
            if (columnLength <= 1) {
                String columnName = columnLength == 1 ? columns[0] : CharSequenceUtil.toUnderlineCase(fieldName);
                parse(queryType, columnName, fieldValue, consumers);
                return consumers;
            }
            // 解析多列查询
            for (String column : columns) {
                parse(queryType, column, fieldValue, consumers);
            }
            return consumers;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Build query wrapper occurred an error: {}. Query: {}, Field: {}.", e
                .getMessage(), query, field, e);
        }
        return Collections.emptyList();
    }

    private static <R> void parse(QueryType queryType,
                                  String columnName,
                                  Object fieldValue,
                                  List<Consumer<QueryWrapper<R>>> consumers) {
        switch (queryType) {
            case EQ -> consumers.add(q -> q.eq(columnName, fieldValue));
            case NE -> consumers.add(q -> q.ne(columnName, fieldValue));
            case GT -> consumers.add(q -> q.gt(columnName, fieldValue));
            case GE -> consumers.add(q -> q.ge(columnName, fieldValue));
            case LT -> consumers.add(q -> q.lt(columnName, fieldValue));
            case LE -> consumers.add(q -> q.le(columnName, fieldValue));
            case BETWEEN -> {
                // 数组转集合
                List<Object> between = new ArrayList<>(ArrayUtil.isArray(fieldValue)
                    ? List.of((Object[])fieldValue)
                    : (List<Object>)fieldValue);
                ValidationUtils.throwIf(between.size() != 2, "[{}] 必须是一个范围", columnName);
                consumers.add(q -> q.between(columnName, between.getFirst(), between.get(1)));
            }
            case LIKE -> consumers.add(q -> q.like(columnName, fieldValue));
            case LIKE_LEFT -> consumers.add(q -> q.likeLeft(columnName, fieldValue));
            case LIKE_RIGHT -> consumers.add(q -> q.likeRight(columnName, fieldValue));
            case IN -> {
                ValidationUtils.throwIfEmpty(fieldValue, "[{}] 不能为空", columnName);
                consumers.add(q -> q.in(columnName, ArrayUtil.isArray(fieldValue)
                    ? List.of((Object[])fieldValue)
                    : (Collection<Object>)fieldValue));
            }
            case NOT_IN -> {
                ValidationUtils.throwIfEmpty(fieldValue, "[{}] 不能为空", columnName);
                consumers.add(q -> q.notIn(columnName, ArrayUtil.isArray(fieldValue)
                    ? List.of((Object[])fieldValue)
                    : (Collection<Object>)fieldValue));
            }
            case IS_NULL -> consumers.add(q -> q.isNull(columnName));
            case IS_NOT_NULL -> consumers.add(q -> q.isNotNull(columnName));
            default -> throw new IllegalArgumentException("暂不支持 [%s] 查询类型".formatted(queryType));
        }
    }
}
