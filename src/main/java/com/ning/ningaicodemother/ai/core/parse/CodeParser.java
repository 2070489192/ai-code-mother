package com.ning.ningaicodemother.ai.core.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface CodeParser <T>{

     Pattern HTML_CODE_PATTERN = Pattern.compile("```(?:html|html5)\\b[^\\r\\n]*\\r?\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
     Pattern CSS_CODE_PATTERN = Pattern.compile("```(?:css|style)\\b[^\\r\\n]*\\r?\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
     Pattern JS_CODE_PATTERN = Pattern.compile("```(?:js|javascript|script)\\b[^\\r\\n]*\\r?\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
     Pattern INLINE_STYLE_PATTERN = Pattern.compile("<style[^>]*>([\\s\\S]*?)</style>", Pattern.CASE_INSENSITIVE);
     Pattern INLINE_SCRIPT_PATTERN = Pattern.compile("<script(?![^>]*\\bsrc=)[^>]*>([\\s\\S]*?)</script>", Pattern.CASE_INSENSITIVE);
     Pattern STYLE_TAG_PATTERN = Pattern.compile("<style[^>]*>[\\s\\S]*?</style>", Pattern.CASE_INSENSITIVE);
     Pattern SCRIPT_TAG_PATTERN = Pattern.compile("<script(?![^>]*\\bsrc=)[^>]*>[\\s\\S]*?</script>", Pattern.CASE_INSENSITIVE);
     Pattern HEAD_CLOSE_PATTERN = Pattern.compile("</head>", Pattern.CASE_INSENSITIVE);
     Pattern BODY_CLOSE_PATTERN = Pattern.compile("</body>", Pattern.CASE_INSENSITIVE);
     Pattern EXTERNAL_CSS_PATTERN = Pattern.compile("(?is)<link[^>]+rel\\s*=\\s*[\"']?stylesheet");
     Pattern EXTERNAL_JS_PATTERN = Pattern.compile("(?is)<script[^>]+src\\s*=\\s*[\"']script\\.js");


    T parse(String codeContent);

    /**
     * 根据正则模式提取代码
     *
     * @param content 原始内容
     * @param pattern 正则模式
     * @return 提取的代码,未匹配返回 null
     */
     default String extractCodeByPattern(String content, Pattern pattern) {
        if (content == null) {
            return null;
        }
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    default boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    default String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

}