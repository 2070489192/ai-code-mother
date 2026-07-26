package com.ning.ningaicodemother.aop;

import com.ning.ningaicodemother.exception.ErrorCode;
import com.ning.ningaicodemother.exception.ThrowUtils;
import com.ning.ningaicodemother.pojo.User;
import com.ning.ningaicodemother.pojo.UserCode;
import com.ning.ningaicodemother.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuthInter {
    @Resource
    private UserService userService;
@Around("@annotation(authCheck)")
    public Object checkAuth(ProceedingJoinPoint joinPoint,AuthCheck authCheck) throws Throwable {
    String mustRole = authCheck.mustRole();
    // 从请求中获取用户信息
    RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
    HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
    User user = userService.getCurrentLoginUser(request);
    //检查用户是否登录
    ThrowUtils.throwIf(user == null
            , ErrorCode.NOT_FOUND_ERROR
            ,"用户未登录");
    //获得必须的权限
    UserCode mustCode=UserCode.getCodeByValue(mustRole);
    //获取当前用户权限
    UserCode userCode=UserCode.getCodeByValue(user.getUserRole());
    //必须要有管理员权限
    ThrowUtils.throwIf(UserCode.ADMIN.equals(mustCode)&&UserCode.ADMIN.equals(userCode)
            , ErrorCode.NOT_FOUND_ERROR
            ,"用户角色错误");
    //放行
    return joinPoint.proceed();
}
}
