package com.ning.ningaicodemother.ai.core.parse;


import com.ning.ningaicodemother.ai.model.MultiResult;


public class MultiCodeParser implements CodeParser<MultiResult> {

    @Override
    public MultiResult parse(String codeContent) {
            String content = safeTrim(codeContent);
            MultiResult result = new MultiResult();

            String htmlCode = extractCodeByPattern(content, HTML_CODE_PATTERN);
            String cssCode = extractCodeByPattern(content, CSS_CODE_PATTERN);
            String jsCode = extractCodeByPattern(content, JS_CODE_PATTERN);


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
}
