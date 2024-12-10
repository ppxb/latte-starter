package com.ppxb.latte.starter.extension.crud.model.resp;

import cn.crane4j.annotation.Assemble;
import cn.crane4j.annotation.Mapping;
import cn.crane4j.annotation.condition.ConditionOnPropertyNotNull;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ppxb.latte.starter.extension.crud.constant.ContainerPool;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.time.LocalDateTime;

public class BaseDetailResp extends BaseResp {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @ConditionOnPropertyNotNull
    @Assemble(container = ContainerPool.USER_NICKNAME, props = @Mapping(ref = "updateUserString"))
    private Long updateUser;

    @Schema(description = "修改人")
    @ExcelProperty(value = "修改人", order = Integer.MAX_VALUE - 2)
    private String updateUserString;

    @Schema(description = "修改时间")
    @ExcelProperty(value = "修改时间", order = Integer.MAX_VALUE - 1)
    private LocalDateTime updateTime;

    public Long getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateUserString() {
        return updateUserString;
    }

    public void setUpdateUserString(String updateUserString) {
        this.updateUserString = updateUserString;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
