package com.ppxb.latte.starter.extension.crud.validation;

import jakarta.validation.groups.Default;

public interface CrudValidationGroup extends Default {

    /**
     * CRUD 分组校验-新增
     */
    interface Add extends CrudValidationGroup {
    }

    /**
     * CRUD 分组校验-修改
     */
    interface Update extends CrudValidationGroup {
    }
}
