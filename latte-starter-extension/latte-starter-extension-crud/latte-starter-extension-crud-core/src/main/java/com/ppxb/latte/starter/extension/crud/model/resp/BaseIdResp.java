package com.ppxb.latte.starter.extension.crud.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

public class BaseIdResp<T extends Serializable> implements Serializable {

    @Schema(description = "ID")
    private T id;

    public BaseIdResp() {
    }

    public BaseIdResp(T id) {
        this.id = id;
    }

    public T getId() {
        return id;
    }

    public void setId(final T id) {
        this.id = id;
    }
}
