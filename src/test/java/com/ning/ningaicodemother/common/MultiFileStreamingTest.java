package com.ning.ningaicodemother.common;

import com.ning.ningaicodemother.ai.enums.CodeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 只测多文件流式:连续跑多次,观察两件事
 * 1. 模型输出形态:单个自包含 HTML(内联 style/script,新契约的预期形态) / 多块形态 / 残缺(只有 html 且无任何内联内容)
 * 2. 最终落盘结果:三个文件是否都有内容(产品合格标准,由 MultiCodeParser 拆分内联内容得到)
 * 每次使用不同 appid,输出目录为 tmp/out_code/multi_file_{appid}。不做断言,只输出观测数据。
 */
@SpringBootTest
@Slf4j
class MultiFileStreamingTest {

    private static final String PROMPT = "帮我做一个宁科涵的个人技术博客,擅长各种后端,做过各种项目,不超过80行";
    private static final int RUN_TIMES = 5;
    private static final String OUT_CODE_ROOT = System.getProperty("user.dir") + File.separator + "tmp/out_code";

    /** 匹配内联 script 标签(排除 src 引用的外部脚本) */
    private static final Pattern INLINE_SCRIPT_PATTERN = Pattern.compile("<script(?![^>]*\\bsrc=)[^>]*>", Pattern.CASE_INSENSITIVE);

    @Autowired
    private Door door;

    @Test
    void multiFileStreamingRepeatedly() {
        int selfContainedCount = 0;
        int passCount = 0;
        for (int i = 1; i <= RUN_TIMES; i++) {
            long appid = System.currentTimeMillis() + i;
            Flux<String> flux = door.generateCodeStreaming(PROMPT, CodeTypeEnum.MULTI_FILE, appid);
            List<String> chunks = flux.collectList().block();
            String content = chunks == null ? "" : String.join("", chunks);

            boolean hasCssFence = content.contains("```css");
            boolean hasJsFence = content.contains("```javascript") || content.contains("```js");
            boolean hasInlineStyle = content.contains("<style");
            boolean hasInlineScript = INLINE_SCRIPT_PATTERN.matcher(content).find();

            String shape;
            if (hasCssFence || hasJsFence) {
                shape = "多块形态";
            } else if (hasInlineStyle || hasInlineScript) {
                shape = "单个自包含HTML";
                selfContainedCount++;
            } else {
                shape = "残缺(仅html且无内联内容)";
            }

            String dirName = "multi_file_" + appid;
            long[] sizes = fileSizes(dirName);
            boolean pass = sizes[0] > 0 && sizes[1] > 0 && sizes[2] > 0;
            if (pass) {
                passCount++;
            }

            log.info("[第{}次] 分片={} 长度={} | 形态={} (内联style={} 内联script={}) | 落盘 index={}B css={}B js={}B | 产品合格={}",
                    i, chunks == null ? 0 : chunks.size(), content.length(), shape,
                    hasInlineStyle, hasInlineScript, sizes[0], sizes[1], sizes[2], pass);
        }
        log.info("========== 汇总: {} 次中, 单个自包含HTML {} 次, 产品合格(三文件都有内容) {} 次 ==========",
                RUN_TIMES, selfContainedCount, passCount);
    }

    /**
     * 返回输出目录中 index.html / style.css / script.js 的大小(不存在的记 0)
     */
    private long[] fileSizes(String dirName) {
        File dir = new File(OUT_CODE_ROOT, dirName);
        return new long[]{
                new File(dir, "index.html").length(),
                new File(dir, "style.css").length(),
                new File(dir, "script.js").length()
        };
    }
}
