package com.ning.ningaicodemother.common;

import lombok.Data;


import java.io.Serial;
import java.io.Serializable;

// 分页请求参数
@Data
public class PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前页号
     */
    private int pageNum = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private boolean sortOrder = false;
}
