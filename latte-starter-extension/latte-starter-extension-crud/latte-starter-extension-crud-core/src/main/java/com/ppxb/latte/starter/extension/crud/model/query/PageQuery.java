package com.ppxb.latte.starter.extension.crud.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.Range;
import org.springdoc.core.annotations.ParameterObject;

import java.io.Serial;

@ParameterObject
@Schema(description = "分页查询条件")
public class PageQuery extends SortQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_PAGE = 1;

    private static final int DEFAULT_SIZE = 10;

    @Schema(description = "页码")
    @Min(value = 1, message = "页码不能小于 {value}")
    private Integer page = DEFAULT_PAGE;

    @Schema(description = "每页数量")
    @Range(min = 1, max = 1000, message = "每页数量（范围 {min} - {max}）")
    private Integer size = DEFAULT_SIZE;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
