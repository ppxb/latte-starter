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



package com.ppxb.latte.starter.extension.crud.controller;

import cn.hutool.core.lang.tree.Tree;
import com.feiniaojin.gracefulresponse.api.ExcludeFromGracefulResponse;
import com.ppxb.latte.starter.extension.crud.annotation.CrudApi;
import com.ppxb.latte.starter.extension.crud.enums.Api;
import com.ppxb.latte.starter.extension.crud.handler.CrudApiHandler;
import com.ppxb.latte.starter.extension.crud.model.query.PageQuery;
import com.ppxb.latte.starter.extension.crud.model.query.SortQuery;
import com.ppxb.latte.starter.extension.crud.model.req.BaseReq;
import com.ppxb.latte.starter.extension.crud.model.resp.BaseIdResp;
import com.ppxb.latte.starter.extension.crud.model.resp.BasePageResp;
import com.ppxb.latte.starter.extension.crud.service.BaseService;
import com.ppxb.latte.starter.extension.crud.validation.CrudValidationGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class AbstractBaseController<S extends BaseService<L, D, Q, C>, L, D, Q, C extends BaseReq> implements CrudApiHandler {

    @Autowired
    protected S baseService;

    /**
     * 分页查询列表
     *
     * @param query     查询条件
     * @param pageQuery 分页查询条件
     * @return 分页信息
     */
    @CrudApi(Api.PAGE)
    @Operation(summary = "分页查询列表", description = "分页查询列表")
    @ResponseBody
    @GetMapping
    public BasePageResp<L> page(Q query, @Validated PageQuery pageQuery) {
        return baseService.page(query, pageQuery);
    }

    /**
     * 查询列表
     *
     * @param query     查询条件
     * @param sortQuery 排序查询条件
     * @return 列表信息
     */
    @CrudApi(Api.LIST)
    @Operation(summary = "查询列表", description = "查询列表")
    @ResponseBody
    @GetMapping("/list")
    public List<L> list(Q query, SortQuery sortQuery) {
        return baseService.list(query, sortQuery);
    }

    /**
     * 查询树列表
     *
     * @param query     查询条件
     * @param sortQuery 排序查询条件
     * @return 树列表信息
     */
    @CrudApi(Api.TREE)
    @Operation(summary = "查询树列表", description = "查询树列表")
    @ResponseBody
    @GetMapping("/tree")
    public List<Tree<Long>> tree(Q query, SortQuery sortQuery) {
        return baseService.tree(query, sortQuery, false);
    }

    /**
     * 查询详情
     *
     * @param id ID
     * @return 详情信息
     */
    @CrudApi(Api.DETAIL)
    @Operation(summary = "查询详情", description = "查询详情")
    @Parameter(name = "id", description = "ID", example = "1", in = ParameterIn.PATH)
    @ResponseBody
    @GetMapping("/{id}")
    public D detail(@PathVariable("id") Long id) {
        return baseService.get(id);
    }

    /**
     * 新增
     *
     * @param req 创建参数
     * @return ID
     */
    @CrudApi(Api.ADD)
    @Operation(summary = "新增数据", description = "新增数据")
    @ResponseBody
    @PostMapping
    public BaseIdResp<Long> add(@Validated(CrudValidationGroup.Add.class) @RequestBody C req) {
        return new BaseIdResp<>(baseService.add(req));
    }

    /**
     * 修改
     *
     * @param req 修改参数
     * @param id  ID
     */
    @CrudApi(Api.UPDATE)
    @Operation(summary = "修改数据", description = "修改数据")
    @Parameter(name = "id", description = "ID", example = "1", in = ParameterIn.PATH)
    @ResponseBody
    @PutMapping("/{id}")
    public void update(@Validated(CrudValidationGroup.Update.class) @RequestBody C req, @PathVariable("id") Long id) {
        baseService.update(req, id);
    }

    /**
     * 删除
     *
     * @param ids ID 列表
     */
    @CrudApi(Api.DELETE)
    @Operation(summary = "删除数据", description = "删除数据")
    @Parameter(name = "ids", description = "ID 列表", example = "1,2", in = ParameterIn.PATH)
    @ResponseBody
    @DeleteMapping("/{ids}")
    public void delete(@PathVariable("ids") List<Long> ids) {
        baseService.delete(ids);
    }

    /**
     * 导出
     *
     * @param query     查询条件
     * @param sortQuery 排序查询条件
     * @param response  响应对象
     */
    @CrudApi(Api.EXPORT)
    @ExcludeFromGracefulResponse
    @Operation(summary = "导出数据", description = "导出数据")
    @GetMapping("/export")
    public void export(Q query, SortQuery sortQuery, HttpServletResponse response) {
        baseService.export(query, sortQuery, response);
    }
}
