package com.ning.ningaicodemother.pojo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录请求接受类。
 */
@Data
public class LoginUserRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 密码
     */
    private String userPassword;
}
