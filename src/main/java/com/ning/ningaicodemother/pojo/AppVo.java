package com.ning.ningaicodemother.pojo;


import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;


import com.mybatisflex.core.keygen.KeyGenerators;

import lombok.Data;


/**
 * 应用去除敏感信息后的 实体类。
 *
 * @author 柠檬可晗
 * @since 2026-09-01
 */
@Data
public class AppVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    private Long id;

    private String appName;

    private String cover;

    private String codeGenType;

    private UserVo userVo;

    private LocalDateTime editTime;

    private LocalDateTime createTime;



}
