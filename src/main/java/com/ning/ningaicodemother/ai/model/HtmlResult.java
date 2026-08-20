package com.ning.ningaicodemother.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
@Description("生成单页面HTML的结果")
public class HtmlResult {
    @Description("HTML代码")
    private String htmlCode;
    @Description("生成代码的描述")
    private String description;
}
