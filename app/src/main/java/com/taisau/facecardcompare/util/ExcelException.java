package com.taisau.facecardcompare.util;

/**
 * Created by whx on 2017-10-20
 * 导入导出中会出现各种各样的问题，比如：数据源为空、有重复行等，自定义一个ExcelException异常类，用来处理这些问题。
 */
public class ExcelException extends Exception {

    private static final long serialVersionUID = 7582457389069996252L;

    public ExcelException() {
    }

    public ExcelException(String message) {
        super(message);
    }

    public ExcelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcelException(Throwable cause) {
        super(cause);
    }

//    public ExcelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
//        super(message, cause, enableSuppression, writableStackTrace);
//    }
}
