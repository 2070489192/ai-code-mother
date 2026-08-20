package com.ning.ningaicodemother.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
@Description("生成多个代码文件的结果")
public class MultiResult {
    @Description("HTML代码")
    private String htmlCode;
    @Description("CSS代码")
    private String cssCode;
    @Description("JavaScript代码")
    private String javascriptCode;
    @Description("生成代码的描述")
    private String description;
}
