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



package com.ppxb.latte.starter.extension.crud.autoconfigure;

import cn.hutool.core.lang.tree.TreeNodeConfig;
import com.ppxb.latte.starter.core.validation.CheckUtils;
import com.ppxb.latte.starter.extension.crud.annotation.TreeField;

public class CrudTreeProperties {

    /**
     * ID 字段名
     */
    private String idKey = "id";

    /**
     * 父 ID 字段名
     */
    private String parentIdKey = "parentId";

    /**
     * 名称字段名
     */
    private String nameKey = "name";

    /**
     * 排序字段名
     */
    private String weightKey = "weight";

    /**
     * 子列表字段名
     */
    private String childrenKey = "children";

    /**
     * 递归深度（< 0 不限制）
     */
    private Integer deep = -1;

    /**
     * 根节点 ID
     */
    private Long rootId = 0L;

    public String getIdKey() {
        return idKey;
    }

    public void setIdKey(String idKey) {
        this.idKey = idKey;
    }

    public String getParentIdKey() {
        return parentIdKey;
    }

    public void setParentIdKey(String parentIdKey) {
        this.parentIdKey = parentIdKey;
    }

    public String getNameKey() {
        return nameKey;
    }

    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }

    public String getWeightKey() {
        return weightKey;
    }

    public void setWeightKey(String weightKey) {
        this.weightKey = weightKey;
    }

    public String getChildrenKey() {
        return childrenKey;
    }

    public void setChildrenKey(String childrenKey) {
        this.childrenKey = childrenKey;
    }

    public Integer getDeep() {
        return deep;
    }

    public void setDeep(Integer deep) {
        this.deep = deep;
    }

    public Long getRootId() {
        return rootId;
    }

    public void setRootId(Long rootId) {
        this.rootId = rootId;
    }

    public TreeNodeConfig genTreeNodeConfig() {
        return TreeNodeConfig.DEFAULT_CONFIG.setIdKey(idKey)
            .setParentIdKey(parentIdKey)
            .setNameKey(nameKey)
            .setWeightKey(weightKey)
            .setChildrenKey(childrenKey)
            .setDeep(deep < 0 ? null : deep);
    }

    public TreeNodeConfig genTreeNodeConfig(TreeField treeField) {
        CheckUtils.throwIfNull(treeField, "请添加并配置 @TreeField 树结构信息");
        return new TreeNodeConfig().setIdKey(treeField.value())
            .setParentIdKey(treeField.parentIdKey())
            .setNameKey(treeField.nameKey())
            .setWeightKey(treeField.weightKey())
            .setChildrenKey(treeField.childrenKey())
            .setDeep(treeField.deep() < 0 ? null : treeField.deep());
    }
}
