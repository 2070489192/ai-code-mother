package com.ning.ningaicodemother.request.apprequest;

import com.ning.ningaicodemother.request.common.PageRequest;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页查询应用请求参数,用于选择哪些可以作为筛选条件
 */
@Data
public class AppQueryRequest extends PageRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String appName;

    private String cover;

    private String codeGenType;

    private int  priority;

    private Long userId;
}
