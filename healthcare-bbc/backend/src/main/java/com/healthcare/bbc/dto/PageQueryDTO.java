package com.healthcare.bbc.dto;

import lombok.Data;

@Data
public class PageQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String keyword;
    private String sortBy;
    private String sortOrder = "asc";

    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
