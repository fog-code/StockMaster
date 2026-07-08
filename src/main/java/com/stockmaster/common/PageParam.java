package com.stockmaster.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询参数基类
 */
@Data
public class PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
