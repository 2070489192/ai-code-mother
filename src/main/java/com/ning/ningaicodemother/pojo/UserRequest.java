package com.ning.ningaicodemother.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求接受类。
 */
@Data
public class UserRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户密码
     */
    private String userPassword;
    /**
     * 确认密码
     */
    private String checkPassword;
}
