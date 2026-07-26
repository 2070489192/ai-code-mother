package com.ning.ningaicodemother.pojo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户更新类
 * */
@Data
public class UserUpdate implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     *  用户简介
     */
    private String userProfile;
}
