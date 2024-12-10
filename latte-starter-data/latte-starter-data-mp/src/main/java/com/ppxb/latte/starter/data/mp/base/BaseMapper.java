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



package com.ppxb.latte.starter.data.mp.base;

import cn.hutool.core.util.ClassUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.baomidou.mybatisplus.extension.toolkit.Db;

import java.util.Collection;

/**
 * 基础 Mapper 接口
 * <p>
 * 扩展 MyBatis-Plus 的 BaseMapper，提供额外的便捷方法：
 * <ul>
 * <li>批量操作方法</li>
 * <li>链式查询构建器</li>
 * <li>Lambda 表达式支持</li>
 * </ul>
 *
 * <p>
 * 使用示例：
 * <pre>{@code
 * @Mapper
 * public interface UserMapper extends BaseMapper<User> {
 *
 * // 使用批量插入
 * default void insertUsers(List<User> users) {
 * insertBatch(users);
 * }
 *
 * // 使用链式查询
 * default List<User> findActiveUsers() {
 * return lambdaQuery()
 * .eq(User::getStatus, "active")
 * .list();
 * }
 * }
 * }</pre>
 *
 * @param <T> 实体类型
 * @author ppxb
 * @since 1.0.0
 */
public interface BaseMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {

    /**
     * 批量插入实体
     *
     * @param entityList 实体集合
     * @return 是否成功
     */
    default boolean insertBatch(Collection<T> entityList) {
        return Db.saveBatch(entityList);
    }

    /**
     * 批量更新实体
     *
     * @param entityList 实体集合
     * @return 是否成功
     */
    default boolean updateBatchById(Collection<T> entityList) {
        return Db.updateBatchById(entityList);
    }

    /**
     * 获取查询条件构造器
     *
     * @return QueryChainWrapper 实例
     */
    default QueryChainWrapper<T> query() {
        return ChainWrappers.queryChain(this);
    }

    /**
     * 获取 Lambda 查询条件构造器
     *
     * @return LambdaQueryChainWrapper 实例
     */
    default LambdaQueryChainWrapper<T> lambdaQuery() {
        return ChainWrappers.lambdaQueryChain(this, this.currentEntityClass());
    }

    /**
     * 根据实体获取 Lambda 查询条件构造器
     *
     * @param entity 实体对象
     * @return LambdaQueryChainWrapper 实例
     */
    default LambdaQueryChainWrapper<T> lambdaQuery(T entity) {
        return ChainWrappers.lambdaQueryChain(this, entity);
    }

    /**
     * 获取更新条件构造器
     *
     * @return UpdateChainWrapper 实例
     */
    default UpdateChainWrapper<T> update() {
        return ChainWrappers.updateChain(this);
    }

    /**
     * 获取 Lambda 更新条件构造器
     *
     * @return LambdaUpdateChainWrapper 实例
     */
    default LambdaUpdateChainWrapper<T> lambdaUpdate() {
        return ChainWrappers.lambdaUpdateChain(this);
    }

    /**
     * 获取当前实体类类型
     *
     * @return 实体类类型
     */
    default Class<T> currentEntityClass() {
        return (Class<T>)ClassUtil.getTypeArgument(this.getClass(), 0);
    }
}
