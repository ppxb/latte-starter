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



package com.ppxb.latte.starter.data.mp.service.impl;

import cn.hutool.core.util.ClassUtil;
import com.ppxb.latte.starter.core.util.ReflectUtils;
import com.ppxb.latte.starter.core.validation.CheckUtils;
import com.ppxb.latte.starter.data.mp.base.BaseMapper;
import com.ppxb.latte.starter.data.mp.service.IService;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 通用服务实现类
 * <p>
 * 扩展 MyBatis-Plus 的 ServiceImpl，提供增强的服务实现功能。
 *
 * @param <M> Mapper类型
 * @param <T> 实体类型
 * @author ppxb
 * @since 1.0.0
 */
public class ServiceImpl<M extends BaseMapper<T>, T> extends com.baomidou.mybatisplus.extension.service.impl.ServiceImpl<M, T> implements IService<T> {

    /**
     * 实体字段缓存
     */
    private List<Field> entityFields;

    @Override
    public T getById(Serializable id) {
        return this.getById(id, true);
    }

    public List<Field> getEntityFields() {
        if (null == this.entityFields) {
            this.entityFields = ReflectUtils.getNonStaticFields(this.getEntityClass());
        }
        return this.entityFields;
    }

    protected T getById(Serializable id, boolean isCheckExists) {
        T entity = baseMapper.selectById(id);
        if (isCheckExists) {
            CheckUtils.throwIfNotExists(entity, ClassUtil.getClassName(this.getEntityClass(), true), "ID", id);
        }
        return entity;
    }
}
