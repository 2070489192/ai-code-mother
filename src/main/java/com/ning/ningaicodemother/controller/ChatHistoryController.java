package com.ning.ningaicodemother.controller;

import com.mybatisflex.core.paginate.Page;
import com.ning.ningaicodemother.common.BaseResponse;
import com.ning.ningaicodemother.common.ResultUtil;
import com.ning.ningaicodemother.pojo.ChatHistory;
import com.ning.ningaicodemother.pojo.User;
import com.ning.ningaicodemother.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.ning.ningaicodemother.service.ChatHistoryService;

import java.time.LocalDateTime;


/**
 * 对话历史 控制层。
 *
 * @author 柠檬可晗
 * @since 2026-09-16
 */
@RestController
@RequestMapping("/chatHistory")
public class ChatHistoryController {

    private final ChatHistoryService chatHistoryService;

    private final UserService userService;

    @Autowired
    public ChatHistoryController(ChatHistoryService chatHistoryService,UserService userService){
        this.chatHistoryService=chatHistoryService;
        this.userService=userService;
    }


    /**
     * 分页查询某个应用的对话历史（游标查询）
     *
     * @param appId          应用ID
     * @param pageSize       页面大小
     * @param lastCreateTime 最后一条记录的创建时间
     * @param request        请求
     * @return 对话历史分页
     */
    @GetMapping("/app/{appId}")
    public BaseResponse<Page<ChatHistory>> listAppChatHistory(@PathVariable Long appId,
                                                              @RequestParam(defaultValue = "10") int pageSize,
                                                              @RequestParam(required = false) LocalDateTime lastCreateTime,
                                                              HttpServletRequest request) {
        User loginUser = userService.getCurrentLoginUser(request);
        Page<ChatHistory> result = chatHistoryService.listAppChatHistoryByPage(appId, pageSize, lastCreateTime, loginUser);
        return ResultUtil.success(result);
    }


}
