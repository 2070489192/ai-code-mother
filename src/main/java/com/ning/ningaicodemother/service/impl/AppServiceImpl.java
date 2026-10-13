package com.ning.ningaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.UpdateEntity;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ning.ningaicodemother.ai.enums.CodeTypeEnum;
import com.ning.ningaicodemother.common.Door;
import com.ning.ningaicodemother.common.Priority;
import com.ning.ningaicodemother.enums.ChatHistoryMessageTypeEnum;
import com.ning.ningaicodemother.enums.ErrorCode;
import com.ning.ningaicodemother.exception.BusinessException;
import com.ning.ningaicodemother.exception.ThrowUtils;
import com.ning.ningaicodemother.pojo.*;
import com.ning.ningaicodemother.mapper.AppMapper;
import com.ning.ningaicodemother.request.apprequest.AppQueryRequest;
import com.ning.ningaicodemother.request.apprequest.AppUpdateRequest;
import com.ning.ningaicodemother.service.AppService;
import com.ning.ningaicodemother.service.ChatHistoryService;
import com.ning.ningaicodemother.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author 柠檬可晗
 * @since 2026-09-01
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{


    private final UserService userService;

    private final Door door;

    private final ChatHistoryService chatHistoryService;

    public AppServiceImpl(ChatHistoryService chatHistoryService,Door door,UserService userService) {
        this.chatHistoryService = chatHistoryService;
        this.door = door;
        this.userService = userService;
    }

    @Override
    public boolean removeById(Long id, User user) {
        // 检查应用是否存在
       QueryWrapper queryWrapper=QueryWrapper
               .create()
               .eq("id",id)
               .eq("userId",user.getId());
        ThrowUtils.throwIf(!exists(queryWrapper), ErrorCode.NOT_FOUND_ERROR);
        App app = UpdateEntity.of(App.class,id);
        app.setIsDelete(1);
        app.setEditTime(LocalDateTime.now());
        int update = getMapper().update(app);
        ThrowUtils.throwIf(!(update>0), ErrorCode.OPERATION_ERROR);
        // 删除应用下的所有聊天记录
        try {
            chatHistoryService.deleteByAppId(id);
        } catch (Exception e) {
           throw new BusinessException(ErrorCode.OPERATION_ERROR,"删除应用下的聊天记录失败"+e.getMessage());
        }
        return true;
    }

    @Override
    public boolean updateById(AppUpdateRequest appUpdate, User user) {
        QueryWrapper queryWrapper=QueryWrapper
                .create()
                .eq("id",appUpdate.getId())
                .eq("userId",user.getId());
        ThrowUtils.throwIf(!exists(queryWrapper), ErrorCode.NOT_FOUND_ERROR);
        App app = UpdateEntity.of(App.class,appUpdate.getId());
        app.setAppName(appUpdate.getAppName());
        app.setCover(appUpdate.getCover());
        app.setEditTime(LocalDateTime.now());
        int update = getMapper().update(app);
        ThrowUtils.throwIf(!(update>0), ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public boolean updateByAdmin(AppUpdateRequest appUpdateRequest) {
        App app = UpdateEntity.of(App.class,appUpdateRequest.getId());
        app.setAppName(appUpdateRequest.getAppName());
        app.setCover(appUpdateRequest.getCover());
        app.setPriority(Priority.HIGH_PRIORITY);
        int update =getMapper().update(app);
        ThrowUtils.throwIf(!(update>0), ErrorCode.OPERATION_ERROR);
        return true;
    }


    @Override
    public QueryWrapper getQueryWrapperByAppQueryRequest(AppQueryRequest appQueryRequest) {
       ThrowUtils.throwIf(appQueryRequest==null, ErrorCode.PARAMS_ERROR,"查询参数不能为空");
       return QueryWrapper.create()
               .eq("priority",appQueryRequest.getPriority())
               .eq("userId",appQueryRequest.getUserId())
               .eq("id",appQueryRequest.getId())
               .like("appName",appQueryRequest.getAppName())
               .like("cover",appQueryRequest.getCover())
               .eq("codeGenType",appQueryRequest.getCodeGenType())
               .orderBy(appQueryRequest.getSortField(),appQueryRequest.isSortOrder());
    }

    @Override
    public List<AppVo> getListAppVoByListApp(List<App> listApp) {
        ThrowUtils.throwIf(listApp==null||listApp.isEmpty(), ErrorCode.PARAMS_ERROR,"应用列表不能为空");
        Set<Long> userIds =listApp.stream().
                map(App::getUserId).
                collect(Collectors.toSet());
        Map<Long, UserVo> userVomap=userService.listByIds(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId,userService::getUserVoByUser));
        return listApp.stream().map(
                app->{
                    AppVo appVo = getAppVoByApp(app);
                    UserVo userVo =userVomap.get(app.getUserId());
                    appVo.setUserVo(userVo);
                    return appVo;
                }
        ).collect(Collectors.toList());
    }

    @Override
    public Flux<String> chatToGenCode(Long appid,String prompt,  CodeTypeEnum codeGenType,User user) {
        ThrowUtils.throwIf(appid==null, ErrorCode.PARAMS_ERROR,"应用id不能为空");
        ThrowUtils.throwIf(codeGenType==null, ErrorCode.PARAMS_ERROR,"代码生成类型不能为空");
        QueryWrapper queryWrapper=QueryWrapper
                .create()
                .eq("id",appid)
                .eq("userId",user.getId());
        App app=getMapper().selectOneByQuery(queryWrapper);
        ThrowUtils.throwIf(app==null, ErrorCode.NOT_FOUND_ERROR,"应用id不存在或用户不是该应用的创建人");
        chatHistoryService.addChatMessage(appid,prompt, ChatHistoryMessageTypeEnum.USER.getValue(),user.getId());
        Flux<String> stringFlux = door.generateCodeStreaming(prompt, codeGenType, appid);
        StringBuilder sb=new StringBuilder();
       return stringFlux.map(
                chunk->{
                    sb.append(chunk);
                    return chunk;
                })
                        .doOnComplete(()->{
                            String codeContent = sb.toString();
                            if(!StrUtil.isBlank(codeContent)){
                                //保存聊天记录
                                chatHistoryService.addChatMessage(appid,codeContent, ChatHistoryMessageTypeEnum.AI.getValue(),user.getId());
                            }
                        })
               .doOnError(e->{
                    String errorMsg = "生成代码失败"+e.getMessage();
                    //保存聊天记录
                    chatHistoryService.addChatMessage(appid,errorMsg, ChatHistoryMessageTypeEnum.AI.getValue(),user.getId());
               });

    }

    @Override
    public String saveFileDeploy(String appid ,User user) {
        App app=getById(appid);
        ThrowUtils.throwIf(app==null, ErrorCode.NOT_FOUND_ERROR,"不存在应用");
        ThrowUtils.throwIf(!app.getUserId().equals(user.getId()), ErrorCode.OPERATION_ERROR,"用户不是该应用的创建人");
        //检查输出目录是否存在
        String codeGenType = app.getCodeGenType();
        String unique = StrUtil.format("{}_{}",codeGenType,appid);
        String sourceFileDir=Priority.TEP_OUT_CODE+ File.separator+ unique;
        File sourceFile=new File(sourceFileDir);
        ThrowUtils.throwIf(!sourceFile.exists(), ErrorCode.OPERATION_ERROR,"应用还未生成");
        //检查是否有部署标识,如果没有则生成.
        String deployKey = app.getDeployKey();
        if(StrUtil.isBlank(deployKey)){
            deployKey= RandomUtil.randomString(6);
        }
        //构造输出目录
        String deployFileDir=Priority.TEP_FILE_DEPLOY+ File.separator+ deployKey;
        //将文件复制到新目录.
        File deployFile=new File(deployFileDir);
        try{
            FileUtil.copyContent(sourceFile,deployFile,true);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"创建部署目录失败");
        }
        App updateApp =new App();
        updateApp.setId(app.getId());
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        updateApp.setEditTime(LocalDateTime.now());
        boolean b = updateById(updateApp);
        ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR,"部署信息失败");
        return String.format("%s/%s/",Priority.DOMAIN,deployKey);
    }

    @Override
    public AppVo getByIdAppVo(Long id) {
        App app = getMapper().selectOneById(id);
        ThrowUtils.throwIf(app==null, ErrorCode.NOT_FOUND_ERROR);
        return getAppVoByApp(app);
    }

    public AppVo getAppVoByApp(App app){

        return BeanUtil.copyProperties(app,AppVo.class);
    }

}