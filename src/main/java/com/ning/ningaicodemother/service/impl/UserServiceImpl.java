package com.ning.ningaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.core.util.UpdateEntity;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ning.ningaicodemother.common.PageRequest;
import com.ning.ningaicodemother.exception.ErrorCode;
import com.ning.ningaicodemother.exception.ThrowUtils;
import com.ning.ningaicodemother.pojo.*;
import com.ning.ningaicodemother.mapper.UserMapper;
import com.ning.ningaicodemother.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;



/**
 * 用户 服务层实现。
 *
 * @author 柠檬可晗
 * @since 2026-07-17
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService {

    /**
     * 用户注册
     * @param userRequest 用户注册请求
     * @return 用户ID
     */
    @Override
    public Long registerUser(UserRequest userRequest) {
       ThrowUtils.throwIf(checkUserRepeatByAccount(userRequest.getUserAccount()), ErrorCode.USER_EXIST_ERROR);
       ThrowUtils.throwIf(
               userRequest.getUserPassword()==null || userRequest.getUserPassword().isEmpty(),
               ErrorCode.PARAMS_ERROR,
               "密码不能为空"
       );
       ThrowUtils.throwIf(
               userRequest.getCheckPassword()==null || userRequest.getCheckPassword().isEmpty(),
               ErrorCode.PARAMS_ERROR,
               "确认密码不能为空"
       );
       ThrowUtils.throwIf(
               userRequest.getUserPassword().length() < 6 || userRequest.getUserPassword().length() > 12,
               ErrorCode.PARAMS_ERROR,
               "密码长度必须在6-12位之间"
       );
       ThrowUtils.throwIf(
               !userRequest.getCheckPassword().equals(userRequest.getUserPassword()),
               ErrorCode.PARAMS_ERROR,
               "两次密码不一致"
       );
       User newUser = new User();
       newUser.setUserAccount(userRequest.getUserAccount());
       newUser.setUserPassword(encryptPassword(userRequest.getUserPassword()));
       newUser.setUserRole(UserCode.USER.getValue());
       save(newUser);

       return newUser.getId();

    }

    /**
     * 用户登录
     * @param loginUserRequest 用户登录请求
     */

    @Override
    public void loginUser(LoginUserRequest loginUserRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(
                loginUserRequest.getUserAccount()==null || loginUserRequest.getUserAccount().isEmpty(),
                ErrorCode.PARAMS_ERROR,
                "账号不能为空"
        );
        ThrowUtils.throwIf(
                loginUserRequest.getUserPassword()==null || loginUserRequest.getUserPassword().isEmpty(),
                ErrorCode.PARAMS_ERROR,
                "密码不能为空"
        );
        ThrowUtils.throwIf(
                loginUserRequest.getUserPassword().length() < 6 || loginUserRequest.getUserPassword().length() > 12,
                ErrorCode.PARAMS_ERROR,
                "密码长度必须在6-12位之间"
        );
        // 检查用户账号是否存在
       ThrowUtils.throwIf(!checkUserRepeatByAccount(loginUserRequest.getUserAccount()), ErrorCode.NOT_FOUND_ERROR);
       // 检查密码是否正确
       User user = getMapper().selectOneByQuery(  QueryWrapper.create()
               .eq("user_account",loginUserRequest.getUserAccount())
               .eq("user_password",encryptPassword(loginUserRequest.getUserPassword())));
       ThrowUtils.throwIf(user==null, ErrorCode.PASSWORD_ERROR);
       // 登录成功
       request.getSession().setAttribute("USER_LOGIN_STATE",user);
    }
    /**
     * 获取用户信息
     * @param userAccount 用户账号
     * @return  脱敏后的用户信息
     */
    @Override
    public UserVo getUserInfo(String userAccount) {
        // 检查用户账号是否存在
        ThrowUtils.throwIf(!checkUserRepeatByAccount(userAccount), ErrorCode.NOT_FOUND_ERROR);
        // 查询用户信息
        User user = getMapper().selectOneByQuery(QueryWrapper
                .create()
                .eq("user_account",userAccount));
        // 构建用户VO
        return getUserVoByUser(user);
    }

    /**
     * 检查用户账号是否存在
     * @param userAccount 用户账号
     * @return 是否存在
     */

    @Override
    public boolean checkUserRepeatByAccount(String userAccount) {
        return  exists(QueryWrapper.create().eq("user_account",userAccount));
    }

    /**
     * 加密密码
     * @param password 密码
     * @return 加密后的密码
     */
    @Override
    public String encryptPassword(String password) {
        //Md5加密
        return SecureUtil.md5(password);
    }

    /**
     * 根据主键获取用户信息
     * @param id 用户主键
     * @return 脱敏后的用户详情
     */
    @Override
    public UserVo getUserInfoById(Long id) {
        // 检查用户是否存在
        ThrowUtils.throwIf(!exists(QueryWrapper.create().eq("id",id)), ErrorCode.NOT_FOUND_ERROR);
        // 查询用户信息
        User user = getById(id);
        // 构建用户VO
        return getUserVoByUser(user);
    }

    /**
     * 删除用户 （逻辑删除）
     * @param id 用户主键
     * @return  是否删除成功
     */
    @Override
    public boolean removeById(Long id) {
        QueryWrapper queryWrapper=QueryWrapper
                .create()
                .eq("id",id);
        // 检查用户是否存在
        ThrowUtils.throwIf(!exists(queryWrapper), ErrorCode.NOT_FOUND_ERROR);
        //获得用户信息

        // 更新用户信息
     User user = UpdateEntity.of(User.class,id);
               user.setIsDelete(1);
               user.setUpdateTime(LocalDateTime.now());
        int affectedRows = getMapper().update(user);
        ThrowUtils.throwIf(!(affectedRows>0), ErrorCode.OPERATION_ERROR);
        return true;

    }

    /**
     * 根据用户对象获取用户VO
     * @param user 用户对象 包含用户信息
     * @return  包含脱敏后的用户信息
     */
    @Override
    public UserVo getUserVoByUser(User user) {
        // 检查用户是否存在
        ThrowUtils.throwIf(user==null, ErrorCode.NOT_FOUND_ERROR);
        UserVo userVo = new UserVo();
        BeanUtil.copyProperties(user,userVo);
        return userVo;
    }

    /**
     * 更新用户信息
     * @param userUpdate 用户更新请求 包含用户信息
     * @param request HTTP请求对象
     * @return  包含脱敏后的用户信息
     */
    @Override
    public UserVo userUpdate(UserUpdate userUpdate, HttpServletRequest request) {
        User user =  (User) request.getSession().getAttribute("USER_LOGIN_STATE");
        UpdateChain.of(User.class)
                .set(User::getUserName,userUpdate.getUserName())
                .set(User::getUserAvatar,userUpdate.getUserAvatar())
                .set(User::getUserProfile,userUpdate.getUserProfile())
                .set(User::getEditTime, LocalDateTime.now())
                .set(User::getUpdateTime, LocalDateTime.now())
                .where(User::getId).eq(user.getId())
                .update();
        // 刷新用户信息
        user = getById(user.getId());
        request.getSession().setAttribute("USER_LOGIN_STATE",user);
        // 构建用户VO
        return getUserVoByUser(user);
    }

    /**
     * 分页查询用户信息
     * @param pageRequest 分页请求参数 包含分页参数和排序字段
     * @return  分页对象 包含脱敏后的用户信息
     */

    @Override
    public Page<UserVo> pageUserVo(PageRequest pageRequest) {
        //分页参数
        long pageNum = pageRequest.getPageNum();
        long pageSize = pageRequest.getPageSize();
        //查询排序
        QueryWrapper queryWrapper=QueryWrapper
                .create()
                .orderBy(pageRequest.getSortField(),pageRequest.isSortOrder());
        //查询出结果
       Page<User> page = getMapper().paginate(pageNum,pageSize,queryWrapper);
       //获得用户列表
       List<User> records = page.getRecords();
       //构建用户VO列表,复用getUserVoByUser方法
       List<UserVo> userVoList=records.stream()
               .map(this::getUserVoByUser)
               .toList();
       //创建脱敏后的分页对象
        return new Page<>(userVoList,pageNum,pageSize,page.getTotalRow());
    }

    @Override
    public boolean logout(HttpServletRequest request) {
        ThrowUtils.throwIf(
                request.getSession().getAttribute("USER_LOGIN_STATE")==null
                ,ErrorCode.OPERATION_ERROR
                , "用户未登录");
        request.getSession().invalidate();
        return true;
    }

    @Override
    public User getCurrentLoginUser(HttpServletRequest request) {
        User user =  (User) request.getSession().getAttribute("USER_LOGIN_STATE");
        ThrowUtils.throwIf(user==null, ErrorCode.NOT_FOUND_ERROR);
        return user;
    }


}