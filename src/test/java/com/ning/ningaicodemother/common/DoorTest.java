package com.ning.ningaicodemother.common;

import com.ning.ningaicodemother.ai.enums.CodeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class DoorTest {
    @Autowired
    private  Door door;

    @Test
    void testGenerateHtmlCode() {
        log.info("testGenerateHtmlCode");
        door.generateCode("你好,帮我做一个登录页面,总代码40行", CodeTypeEnum.MULTI_FILE);
    }

}