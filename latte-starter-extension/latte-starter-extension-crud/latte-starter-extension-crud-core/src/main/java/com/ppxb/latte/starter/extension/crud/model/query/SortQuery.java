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



package com.ppxb.latte.starter.extension.crud.model.query;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import com.ppxb.latte.starter.core.constant.StringConstants;
import com.ppxb.latte.starter.core.validation.ValidationUtils;
import com.ppxb.latte.starter.data.core.util.SqlInjectionUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "排序查询条件")
public class SortQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "排序条件", example = "createTime,desc")
    private String[] sort;

    public Sort getSort() {
        if (ArrayUtil.isEmpty(sort)) {
            return Sort.unsorted();
        }
        ValidationUtils.throwIf(sort.length < 2, "排序条件非法");
        List<Sort.Order> orders = new ArrayList<>(sort.length);
        if (CharSequenceUtil.contains(sort[0], StringConstants.COMMA)) {
            // e.g "sort=createTime,desc&sort=name,asc"
            for (String s : sort) {
                List<String> sortList = CharSequenceUtil.splitTrim(s, StringConstants.COMMA);
                orders.add(this.getOrder(sortList.get(0), sortList.get(1)));
            }
        } else {
            // e.g "sort=createTime,desc"
            orders.add(this.getOrder(sort[0], sort[1]));
        }
        return Sort.by(orders);
    }

    public void setSort(String[] sort) {
        this.sort = sort;
    }

    private Sort.Order getOrder(String field, String direction) {
        ValidationUtils.throwIf(SqlInjectionUtils.check(field), "排序字段包含非法字符");
        return new Sort.Order(Sort.Direction.valueOf(direction.toUpperCase()), field);
    }
}
