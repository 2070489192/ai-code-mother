package com.ning.ningaicodemother.common;

import com.ning.ningaicodemother.ai.enums.CodeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

/**
 * 只测多文件非流式(结构化输出)路径:确认使用独立的系统提示词后,三个文件依然都能写出来。
 */
@SpringBootTest
@Slf4j
class MultiFileStructuredTest {

    private static final String PROMPT = "帮我做一个宁科涵的个人技术博客,擅长各种后端,做过各种项目,不超过80行";
    private static final int RUN_TIMES = 2;

    @Autowired
    private Door door;

    @Test
    void multiFileStructuredRepeatedly() {
        int passCount = 0;
        for (int i = 1; i <= RUN_TIMES; i++) {
            long appid = System.currentTimeMillis() + i;
            File dir = door.generateCode(PROMPT, CodeTypeEnum.MULTI_FILE, appid);
            long indexSize = new File(dir, "index.html").length();
            long cssSize = new File(dir, "style.css").length();
            long jsSize = new File(dir, "script.js").length();
            boolean pass = indexSize > 0 && cssSize > 0 && jsSize > 0;
            if (pass) {
                passCount++;
            }
            log.info("[非流式第{}次] 目录={} | index={}B css={}B js={}B | 产品合格={}",
                    i, dir.getName(), indexSize, cssSize, jsSize, pass);
        }
        log.info("========== 非流式汇总: {} 次中, 产品合格 {} 次 ==========", RUN_TIMES, passCount);
    }
}
