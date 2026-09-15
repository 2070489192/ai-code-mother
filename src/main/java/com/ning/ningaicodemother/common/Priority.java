package com.ning.ningaicodemother.common;

import java.io.File;

public class Priority {

    public static final Integer DEFAULT_PRIORITY = 0;

    public static final Integer HIGH_PRIORITY = 99;
    //临时代码输出目录
    public static final String TEP_OUT_CODE=System.getProperty("user.dir")+ File.separator+"tmp/out_code";
    //文件部署目录
    public static final String TEP_FILE_DEPLOY=System.getProperty("user.dir")+ File.separator+"tmp/file_deploy";
    //域名
    public static final String DOMAIN="http://localhost";
}
