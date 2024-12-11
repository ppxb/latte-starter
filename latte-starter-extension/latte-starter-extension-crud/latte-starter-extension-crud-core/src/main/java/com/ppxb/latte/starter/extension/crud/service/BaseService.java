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



package com.ppxb.latte.starter.extension.crud.service;

import cn.hutool.core.lang.tree.Tree;
import com.ppxb.latte.starter.extension.crud.model.query.PageQuery;
import com.ppxb.latte.starter.extension.crud.model.query.SortQuery;
import com.ppxb.latte.starter.extension.crud.model.resp.BasePageResp;
import com.ppxb.latte.starter.extension.crud.model.resp.LabelValueResp;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 业务接口基类
 *
 * @param <L> 列表类型
 * @param <D> 详情类型
 * @param <Q> 查询条件
 * @param <C> 创建或修改参数类型
 * @since 1.0.0
 */
public interface BaseService<L, D, Q, C> {

    /**
     * 分页查询列表
     *
     * @param query     查询条件
     * @param pageQuery 分页查询条件
     * @return 分页列表信息
     */
    BasePageResp<L> page(Q query, PageQuery pageQuery);

    /**
     * 查询列表
     *
     * @param query     查询条件
     * @param sortQuery 排序查询条件
     * @return 列表信息
     */
    List<L> list(Q query, SortQuery sortQuery);

    /**
     * 查询树列表
     * <p>
     * 虽然提供了查询条件，但不建议使用，容易因缺失根节点导致树节点丢失。
     * 建议在前端进行查询过滤，如需使用建议重写方法。
     * </p>
     *
     * @param query     查询条件
     * @param sortQuery 排序查询条件
     * @param isSimple  是否为简单树结构（不包含基本树结构之外的扩展字段，简单树（下拉列表）使用全局配置结构，复杂树（表格）使用 @DictField 局部配置）
     * @return 树列表信息
     */
    List<Tree<Long>> tree(Q query, SortQuery sortQuery, boolean isSimple);

    /**
     * 查询详情
     *
     * @param id ID
     * @return 详情信息
     */
    D get(Long id);

    /**
     * 查询字典列表
     *
     * @param query     查询条件
     * @param sortQuery 排序查询条件
     * @return 字典列表信息
     * @since 2.1.0
     */
    List<LabelValueResp> listDict(Q query, SortQuery sortQuery);

    /**
     * 新增
     *
     * @param req 创建参数
     * @return 自增 ID
     */
    Long add(C req);

    /**
     * 修改
     *
     * @param req 修改参数
     * @param id  ID
     */
    void update(C req, Long id);

    /**
     * 删除
     *
     * @param ids ID 列表
     */
    void delete(List<Long> ids);

    /**
     * 导出
     *
     * @param query     查询条件
     * @param sortQuery 排序查询条件
     * @param response  响应对象
     */
    void export(Q query, SortQuery sortQuery, HttpServletResponse response);
}
