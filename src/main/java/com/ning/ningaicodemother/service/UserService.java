package com.ning.ningaicodemother.service;


import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.ning.ningaicodemother.common.PageRequest;
import com.ning.ningaicodemother.pojo.*;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户 服务层。
 *
 * @author 柠檬可晗
 * @since 2026-07-17
 */

public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param userRequest 用户注册请求
     * @return 用户ID
     */
    public Long registerUser(UserRequest userRequest);


    /**
     * 用户登录
     * @param loginUserRequest 用户登录请求
     */
    public void loginUser(LoginUserRequest loginUserRequest, HttpServletRequest request);

    /**
     * 获取用户信息
     * @param userAccount 用户账号
     * @return 脱敏后的用户信息
     */
    public UserVo getUserInfo(String userAccount);

    /**
     * 检查用户账号是否重复
     * @param userAccount 用户账号
     */
    public boolean checkUserRepeatByAccount(String userAccount);

    /**
     * 加密密码
     * @param password 密码
     * @return 加密后的密码
     */
    public String encryptPassword(String password);

    /**
     * 根据主键获取用户信息
     * @param id 用户主键
     * @return 脱敏后的用户详情
     */
    public UserVo getUserInfoById(Long id);
    /**
     * 根据主键删除用户
     * @param id 用户主键
     */
    public boolean removeById(Long id);

    /**
     * 根据用户对象获取用户VO
     * @param user 用户对象 包含用户信息
     * @return  包含脱敏后的用户信息
     */
    public UserVo getUserVoByUser(User user);

    /**
     * 更新用户信息
     * @param userUpdate 用户更新请求 包含用户信息
     * @param request HTTP请求对象
     * @return 更新后的用户VO
     */
    public UserVo userUpdate(UserUpdate userUpdate, HttpServletRequest request);

    /**
     * 分页查询用户信息
     * @param pageRequest 分页请求参数 包含分页参数和排序字段
     * @return  分页对象 包含脱敏后的用户信息
     */
    public Page<UserVo> pageUserVo(PageRequest pageRequest);

    /**
     * 用户退出登录
     * @param request HTTP请求对象
     * @return 是否退出登录成功
     */
    public boolean logout(HttpServletRequest request);

    /**
     * 获取当前登录用户信息
     * @param request HTTP请求对象
     * @return 当前登录用户信息
     */
    public User getCurrentLoginUser(HttpServletRequest request);
}
