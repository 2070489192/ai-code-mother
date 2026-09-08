package com.ning.ningaicodemother.controller;

import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.ning.ningaicodemother.ai.enums.CodeTypeEnum;
import com.ning.ningaicodemother.aop.AuthCheck;
import com.ning.ningaicodemother.common.BaseResponse;
import com.ning.ningaicodemother.common.Priority;
import com.ning.ningaicodemother.request.apprequest.AppQueryRequest;
import com.ning.ningaicodemother.request.common.DeleteRequest;
import com.ning.ningaicodemother.common.ResultUtil;
import com.ning.ningaicodemother.enums.ErrorCode;
import com.ning.ningaicodemother.exception.ThrowUtils;
import com.ning.ningaicodemother.pojo.AppVo;
import com.ning.ningaicodemother.pojo.User;
import com.ning.ningaicodemother.request.apprequest.AppSaveRequest;
import com.ning.ningaicodemother.request.apprequest.AppUpdateRequest;
import com.ning.ningaicodemother.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.ning.ningaicodemother.pojo.App;
import com.ning.ningaicodemother.service.AppService;

import java.util.List;

/**
 * 应用 控制层。
 *
 * @author 柠檬可晗
 * @since 2026-09-01
 */
@RestController
@RequestMapping("/app")
public class AppController {

    @Autowired
    private AppService appService;

    @Autowired
    private UserService userService;


    /**
     * 保存应用。
     *
     * @param appSaveRequest 应用保存请求参数
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @AuthCheck
    public BaseResponse<Long> save(@RequestBody AppSaveRequest appSaveRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(appSaveRequest==null, ErrorCode.PARAMS_ERROR,"应用初始化的 prompt 不能为空");
        User user = userService.getCurrentLoginUser(request);
        App app = new App();
        BeanUtil.copyProperties(appSaveRequest, app);
        app.setUserId(user.getId());
        //设置前12个字符作为应用名称
        app.setAppName(appSaveRequest.getInitPrompt().substring(0,Math.min(appSaveRequest.getInitPrompt().length(),12)));
        app.setCodeGenType(CodeTypeEnum.MULTI_FILE.getValue());
        appService.save(app);
        return ResultUtil.success(app.getId());
    }

    /**
     * 根据主键删除应用。
     *
     * @param deleteRequest 删除请求参数包含应用主键;
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @AuthCheck
    public BaseResponse<Boolean> remove(@PathVariable DeleteRequest deleteRequest, HttpServletRequest request) {
        User user = userService.getCurrentLoginUser(request);
        ThrowUtils.throwIf(deleteRequest==null, ErrorCode.PARAMS_ERROR,"应用主键不能为空");
        return ResultUtil.success(appService.removeById(deleteRequest.getId(),user));
    }

    /**
     * 根据主键更新应用。
     *
     * @param appUpdate 应用更新请求参数
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @AuthCheck
    public BaseResponse<Boolean> update(@RequestBody AppUpdateRequest appUpdate, HttpServletRequest request) {
        ThrowUtils.throwIf(appUpdate==null, ErrorCode.PARAMS_ERROR,"没有接收到要改的参数");
        ThrowUtils.throwIf(appUpdate.getId()==null, ErrorCode.PARAMS_ERROR,"应用主键不能为空");
        User user=userService.getCurrentLoginUser(request);
        return ResultUtil.success(appService.updateById(appUpdate,user));
    }

    /**
     * 查询精选应用。(任意权限)
     *
     * @return 精选应用数据列表
     */
    @PostMapping("list")
    public BaseResponse<Page<AppVo>> list(@RequestBody AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest==null, ErrorCode.PARAMS_ERROR,"没有接收到要查询的参数");
        int pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize>20, ErrorCode.PARAMS_ERROR,"每页最多查询20条");
        int pageNum = appQueryRequest.getPageNum();
        // 只能查询精选应用
        appQueryRequest.setPriority(Priority.HIGH_PRIORITY);
        //获取查询对象
        QueryWrapper queryWrapperByAppQueryRequest = appService.getQueryWrapperByAppQueryRequest(appQueryRequest);
        //获得未脱敏的分页对象
        Page<App> page = appService.page(Page.of(pageNum, pageSize), queryWrapperByAppQueryRequest);
        //设置脱敏的分页对象
        Page<AppVo> pageVo=new Page<>(pageNum,pageSize,page.getTotalRow());
        //获取脱敏的应用列表
        List<AppVo> listAppVo = appService.getListAppVoByListApp(page.getRecords());
        pageVo.setRecords(listAppVo);
        return ResultUtil.success(pageVo);
    }

    /**
     * 根据主键获取应用详情(个人)。
     * @param id 应用主键
     * @return 应用详情
     */
    @GetMapping("getInfo/{id}")
    public BaseResponse<App> getInfo(@PathVariable Long id) {
        ThrowUtils.throwIf(id==null, ErrorCode.PARAMS_ERROR,"应用主键不能为空");
        return ResultUtil.success(appService.getById(id));
    }

    /**
     * 分页查询应用(用户自己的)。
     *
     * @param appQueryRequest 应用查询请求参数
     * @return 分页对象
     */
    @GetMapping("pageByUser")
    @AuthCheck
    public BaseResponse<Page<AppVo>> pageByUser(AppQueryRequest appQueryRequest ,HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(appQueryRequest==null, ErrorCode.PARAMS_ERROR,"没有接收到要查询的参数");
        //获取当前登录用户
        User user = userService.getCurrentLoginUser(httpServletRequest);
        int pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize>20, ErrorCode.PARAMS_ERROR,"每页最多查询20条");
        int pageNum = appQueryRequest.getPageNum();
        // 只能查询自己的应用
        appQueryRequest.setUserId(user.getId());
        //获取查询对象
        QueryWrapper queryWrapperByAppQueryRequest = appService.getQueryWrapperByAppQueryRequest(appQueryRequest);
        //获得未脱敏的分页对象
        Page<App> page = appService.page(Page.of(pageNum, pageSize), queryWrapperByAppQueryRequest);
        //设置脱敏的分页对象
        Page<AppVo> pageVo=new Page<>(pageNum,pageSize,page.getTotalRow());
        //获取脱敏的应用列表
        List<AppVo> listAppVo = appService.getListAppVoByListApp(page.getRecords());
        pageVo.setRecords(listAppVo);
        return ResultUtil.success(pageVo);
    }

    /**
     * 分页查询应用全部(管理员)。
     *
     */
    @PostMapping("pageByAdmin")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Page<App>> pageByAdmin(@RequestBody AppQueryRequest appQueryRequest) {
        ThrowUtils.throwIf(appQueryRequest==null, ErrorCode.PARAMS_ERROR,"没有接收到要查询的参数");
        int pageSize = appQueryRequest.getPageSize();
        int pageNum = appQueryRequest.getPageNum();
        //获取查询对象
        QueryWrapper queryWrapperByAppQueryRequest = appService.getQueryWrapperByAppQueryRequest(appQueryRequest);
        //获得未脱敏的分页对象
        Page<App> page = appService.page(Page.of(pageNum, pageSize), queryWrapperByAppQueryRequest);

        return ResultUtil.success(page);
    }

    /**
     * 根据主键获取应用详情(管理员)。
     * @param id 应用主键
     * @return 应用详情
     */
    @GetMapping("getInfoByAdmin/{id}")
    public BaseResponse<App> getInfoByAdmin(@PathVariable Long id) {
        ThrowUtils.throwIf(id==null, ErrorCode.PARAMS_ERROR,"应用主键不能为空");
        return ResultUtil.success(appService.getById(id));
    }

    /**
     * 调整为精选应用(管理员)。
     * @param appUpdateRequest 应用更新请求参数
     * @return 更新结果
     */
    @PutMapping("update")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> updateByAdmin(@RequestBody AppUpdateRequest appUpdateRequest) {
        ThrowUtils.throwIf(appUpdateRequest==null, ErrorCode.PARAMS_ERROR,"没有接收到要改的参数");
        ThrowUtils.throwIf(appUpdateRequest.getId()==null, ErrorCode.PARAMS_ERROR,"应用主键不能为空");
        return ResultUtil.success(appService.updateByAdmin(appUpdateRequest));
    }


}
