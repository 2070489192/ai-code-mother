package com.ning.ningaicodemother.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.ning.ningaicodemother.pojo.App;
import com.ning.ningaicodemother.pojo.AppVo;
import com.ning.ningaicodemother.pojo.User;
import com.ning.ningaicodemother.request.apprequest.AppQueryRequest;
import com.ning.ningaicodemother.request.apprequest.AppUpdateRequest;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author 柠檬可晗
 * @since 2026-09-01
 */
public interface AppService extends IService<App> {
    /**
     * 根据主键删除应用
     * @param id
     * @param user
     * @return
     */
   boolean removeById(Long id, User user);

    /**
     * 更新应用
     * @param app
     * @param user
     * @return
     */
   boolean updateById(AppUpdateRequest app, User user);

   /**
    * 管理员更新应用
    * @param appUpdateRequest
    * @return
    */
   boolean updateByAdmin(AppUpdateRequest appUpdateRequest);

    /**
     * 根据主键获取应用VO
     * @param id
     * @return
     */
   AppVo getByIdAppVo(Long id);

    /**
     * 根据应用查询请求参数构建QueryWrapper对象
     * @param appQueryRequest
     * @return
     */
   QueryWrapper getQueryWrapperByAppQueryRequest(AppQueryRequest appQueryRequest);

    /**
     * 根据应用列表获取应用VO列表
     * @param listApp
     * @return
     */
   List<AppVo> getListAppVoByListApp(List<App> listApp);

}
