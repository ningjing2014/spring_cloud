package com.ln.xproject.util;

import java.util.Date;

public class DateFormatUtils extends org.apache.commons.lang3.time.DateFormatUtils {

    public static String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    public static String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static String NO_SEPARATOR_PATTERN = "yyyyMMddHHmmss";

    /**
     * 格式化当前时间
     * 
     * @param pattern
     * @return
     */
    public static String formatCurrent(String pattern) {
        return format(new Date(), pattern);
    }

}
