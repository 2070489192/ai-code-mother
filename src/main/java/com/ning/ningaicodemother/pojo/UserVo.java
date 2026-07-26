package com.ning.ningaicodemother.pojo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 脱敏后的用户类
 */
@Data
public class UserVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;
    /**
     * 编辑时间
     */
    private LocalDateTime editTime;


}
