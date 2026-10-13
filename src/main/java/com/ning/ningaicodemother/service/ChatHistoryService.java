package com.ning.ningaicodemother.service;

import cn.hutool.db.PageResult;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.ning.ningaicodemother.pojo.ChatHistory;
import com.ning.ningaicodemother.pojo.User;
import com.ning.ningaicodemother.request.chathistory.ChatHistoryQueryRequest;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author 柠檬可晗
 * @since 2026-09-16
 */
public interface ChatHistoryService extends IService<ChatHistory> {
    Boolean addChatMessage(Long appid ,String message,String messageType,Long userId);

    boolean deleteByAppId(Long appId);

    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                               LocalDateTime lastCreateTime,
                                               User loginUser);

    int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount);
}
