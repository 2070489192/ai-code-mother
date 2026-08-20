package com.ning.ningaicodemother.ai.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ning.ningaicodemother.ai.model.HtmlResult;
import com.ning.ningaicodemother.ai.model.MultiResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代码解析器
 * 提供静态方法解析不同类型的代码内容。
 * <p>
 * 兼容三种输出形态:
 * 1. 标准 Markdown 代码围栏(```html / ```css / ```javascript ...)
 * 2. JSON 输出(htmlCode / cssCode / javascriptCode 字段)
 * 3. 单个 ```html 代码块内联 style/script(自动拆分为三个文件,并给 HTML 补上外部引用)
 *
 * @author yupi
 */
public class CodeParser {

    private static final Pattern HTML_CODE_PATTERN = Pattern.compile("```(?:html|html5)\\b[^\\r\\n]*\\r?\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final Pattern CSS_CODE_PATTERN = Pattern.compile("```(?:css|style)\\b[^\\r\\n]*\\r?\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final Pattern JS_CODE_PATTERN = Pattern.compile("```(?:js|javascript|script)\\b[^\\r\\n]*\\r?\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final Pattern JSON_FENCE_PATTERN = Pattern.compile("```(?:json)\\b[^\\r\\n]*\\r?\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);

    private static final Pattern INLINE_STYLE_PATTERN = Pattern.compile("<style[^>]*>([\\s\\S]*?)</style>", Pattern.CASE_INSENSITIVE);
    private static final Pattern INLINE_SCRIPT_PATTERN = Pattern.compile("<script(?![^>]*\\bsrc=)[^>]*>([\\s\\S]*?)</script>", Pattern.CASE_INSENSITIVE);
    private static final Pattern STYLE_TAG_PATTERN = Pattern.compile("<style[^>]*>[\\s\\S]*?</style>", Pattern.CASE_INSENSITIVE);
    private static final Pattern SCRIPT_TAG_PATTERN = Pattern.compile("<script(?![^>]*\\bsrc=)[^>]*>[\\s\\S]*?</script>", Pattern.CASE_INSENSITIVE);
    private static final Pattern HEAD_CLOSE_PATTERN = Pattern.compile("</head>", Pattern.CASE_INSENSITIVE);
    private static final Pattern BODY_CLOSE_PATTERN = Pattern.compile("</body>", Pattern.CASE_INSENSITIVE);
    private static final Pattern EXTERNAL_CSS_PATTERN = Pattern.compile("(?is)<link[^>]+rel\\s*=\\s*[\"']?stylesheet");
    private static final Pattern EXTERNAL_JS_PATTERN = Pattern.compile("(?is)<script[^>]+src\\s*=\\s*[\"']script\\.js");

    private CodeParser() {
    }

    /**
     * 解析 HTML 单文件代码
     */
    public static HtmlResult parseHtmlCode(String codeContent) {
        String content = safeTrim(codeContent);
        HtmlResult result = new HtmlResult();
        String htmlCode = extractCodeByPattern(content, HTML_CODE_PATTERN);
        if (!isEmpty(htmlCode)) {
            result.setHtmlCode(htmlCode.trim());
            return result;
        }
        // 兼容 JSON 输出
        JSONObject json = tryParseJson(content);
        if (json != null && !isEmpty(json.getStr("htmlCode"))) {
            result.setHtmlCode(json.getStr("htmlCode").trim());
        } else {
            // 没有找到代码块,将整个内容作为 HTML
            result.setHtmlCode(content);
        }
        return result;
    }

    /**
     * 解析多文件代码（HTML + CSS + JS）
     */
    public static MultiResult parseMultiFileCode(String codeContent) {
        String content = safeTrim(codeContent);
        MultiResult result = new MultiResult();

        String htmlCode = extractCodeByPattern(content, HTML_CODE_PATTERN);
        String cssCode = extractCodeByPattern(content, CSS_CODE_PATTERN);
        String jsCode = extractCodeByPattern(content, JS_CODE_PATTERN);

        // 1) 三个围栏都没匹配到 -> 尝试 JSON 输出
        if (isEmpty(htmlCode) && isEmpty(cssCode) && isEmpty(jsCode)) {
            JSONObject json = tryParseJson(content);
            if (json != null) {
                htmlCode = json.getStr("htmlCode");
                cssCode = json.getStr("cssCode");
                jsCode = json.getStr("javascriptCode");
                result.setDescription(json.getStr("description"));
            }
        }

        // 2) 只匹配到 html 且 css/js 缺失 -> 尝试从内联 style/script 拆分
        if (!isEmpty(htmlCode) && (isEmpty(cssCode) || isEmpty(jsCode))) {
            if (isEmpty(cssCode)) {
                cssCode = extractCodeByPattern(htmlCode, INLINE_STYLE_PATTERN);
            }
            if (isEmpty(jsCode)) {
                jsCode = extractCodeByPattern(htmlCode, INLINE_SCRIPT_PATTERN);
            }
            // 拆分成功后,把 html 里的内联内容外置,并补上外部文件引用
            if (!isEmpty(cssCode) || !isEmpty(jsCode)) {
                htmlCode = externalizeInlineCode(htmlCode, !isEmpty(cssCode), !isEmpty(jsCode));
            }
        }

        // 3) 什么都没解析出来 -> 兜底:把原文当 HTML 保存,避免丢失内容
        if (isEmpty(htmlCode)) {
            htmlCode = content;
        }

        result.setHtmlCode(htmlCode == null ? "" : htmlCode.trim());
        result.setCssCode(cssCode == null ? "" : cssCode.trim());
        result.setJavascriptCode(jsCode == null ? "" : jsCode.trim());
        return result;
    }

    /**
     * 将 HTML 中内联的 style/script 移除,并在合适位置补上外部文件引用。
     * 只有能成功插入引用时才移除内联内容,避免破坏页面样式/脚本。
     */
    private static String externalizeInlineCode(String html, boolean needCssLink, boolean needScriptRef) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        String result = html;

        if (needCssLink && !EXTERNAL_CSS_PATTERN.matcher(result).find() && HEAD_CLOSE_PATTERN.matcher(result).find()) {
            result = STYLE_TAG_PATTERN.matcher(result).replaceAll("");
            result = HEAD_CLOSE_PATTERN.matcher(result).replaceFirst("<link rel=\"stylesheet\" href=\"style.css\">\n</head>");
        }
        if (needScriptRef && !EXTERNAL_JS_PATTERN.matcher(result).find() && BODY_CLOSE_PATTERN.matcher(result).find()) {
            result = SCRIPT_TAG_PATTERN.matcher(result).replaceAll("");
            result = BODY_CLOSE_PATTERN.matcher(result).replaceFirst("<script src=\"script.js\"></script>\n</body>");
        }
        return result;
    }

    /**
     * 尝试把内容解析为 JSON 对象(支持 ```json 围栏包裹),解析失败返回 null
     */
    private static JSONObject tryParseJson(String content) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }
        String jsonText = content.trim();
        String fenced = extractCodeByPattern(jsonText, JSON_FENCE_PATTERN);
        if (!isEmpty(fenced)) {
            jsonText = fenced.trim();
        }
        try {
            return JSONUtil.parseObj(jsonText);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据正则模式提取代码
     *
     * @param content 原始内容
     * @param pattern 正则模式
     * @return 提取的代码,未匹配返回 null
     */
    private static String extractCodeByPattern(String content, Pattern pattern) {
        if (content == null) {
            return null;
        }
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}
