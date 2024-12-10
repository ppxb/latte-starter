package com.ppxb.latte.starter.extension.crud.model.resp;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Schema(description = "分页信息")
public class BasePageResp<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "列表数据")
    private List<T> list;

    @Schema(description = "总数")
    private long total;

    public BasePageResp() {
    }

    public BasePageResp(List<T> list, long total) {
        this.list = list;
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
