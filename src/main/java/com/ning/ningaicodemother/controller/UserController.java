package com.ning.ningaicodemother.controller;

import com.mybatisflex.core.paginate.Page;
import com.ning.ningaicodemother.aop.AuthCheck;
import com.ning.ningaicodemother.common.BaseResponse;
import com.ning.ningaicodemother.common.PageRequest;
import com.ning.ningaicodemother.common.ResultUtil;
import com.ning.ningaicodemother.exception.ErrorCode;
import com.ning.ningaicodemother.exception.ThrowUtils;
import com.ning.ningaicodemother.pojo.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import com.ning.ningaicodemother.service.UserService;



/**
 * 用户 控制层。
 *
 * @author 柠檬可晗
 * @since 2026-07-17
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 保存用户。
     *
     * @param user 用户
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @AuthCheck(mustRole = "admin")
    //必须要有管理员权限
       public boolean save(@RequestBody User user) {
        return userService.save(user);
    }

    /**
     * 注册用户。
     *
     * @param userRequest 用户注册请求
     * @return 用户ID
     */
    @PostMapping("register")
    public BaseResponse<Long> registerUser(@RequestBody UserRequest userRequest) {
        Long l = userService.registerUser(userRequest);
        return ResultUtil.success(l);
    }

    /**
     * 获取用户信息。
     *
     * @param userAccount 用户账号
     * @return 脱敏后的用户信息
     */
    @GetMapping("getInfo/{userAccount}")
    @AuthCheck(mustRole = "user")
    public BaseResponse<UserVo> getUserInfo(@PathVariable String userAccount) {
        UserVo userVo = userService.getUserInfo(userAccount);
        return ResultUtil.success(userVo);
    }

    /**
     * 用户登录。
     *
     * @param loginUserRequest 用户登录请求
     * @return 用户登录成功后的信息
     */
    @PostMapping("login")
    public BaseResponse<String> loginUserRequest(@RequestBody LoginUserRequest loginUserRequest, HttpServletRequest request) {
        userService.loginUser(loginUserRequest,request);
        return ResultUtil.success("登录成功");
    }
    /**
     * 根据主键删除用户(逻辑删除)。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @AuthCheck(mustRole = "admin")
    public boolean remove(@PathVariable Long id) {
        return userService.removeById(id);
    }

    /**
     * 更新用户。
     *
     * @param userUpdate 用户更新请求
     * @return 更新后的用户信息
     */
    @PutMapping("update")
    @AuthCheck(mustRole = "user")
    public BaseResponse<UserVo> userUpdate(@RequestBody UserUpdate userUpdate, HttpServletRequest request) {
        UserVo userVo = userService.userUpdate(userUpdate,request);
        return ResultUtil.success(userVo);
    }


    /**
     * 根据主键获取用户。
     *
     * @param id 用户主键
     * @return 用户详情
     */
    @GetMapping("getInfo/ById/{id}")
    @AuthCheck(mustRole = "user")
    public UserVo getInfo(@PathVariable Long id) {
        return userService.getUserInfoById(id);
    }

    /**
     * 分页查询用户。
     *
     * @param pageRequest 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Page<UserVo>> pageUserVo(@RequestBody PageRequest pageRequest) {
        ThrowUtils.throwIf(pageRequest==null, ErrorCode.PARAMS_ERROR, "分页对象不能为空");
       return ResultUtil.success(userService.pageUserVo(pageRequest));
    }

    /**
     * 用户退出登录。
     *
     * @param request HTTP请求对象
     * @return 是否退出登录成功
     */
    @PostMapping("logout")
    @AuthCheck(mustRole = "user")
    public BaseResponse<Boolean> logout(HttpServletRequest request) {
        ThrowUtils.throwIf(request==null
                , ErrorCode.PARAMS_ERROR
                , "请求对象不能为空");
        return ResultUtil.success(userService.logout(request));
    }

    /**
     * 获取当前登录用户信息。
     *
     * @param request HTTP请求对象
     * @return 当前登录用户信息
     */
    @GetMapping("getCurrentLoginUser")
    @AuthCheck(mustRole = "user")
    public BaseResponse<User> getCurrentLoginUser(HttpServletRequest request) {
        return ResultUtil.success(userService.getCurrentLoginUser(request));
    }

}
