package com.ln.xproject.util;

import java.util.Arrays;
import java.util.List;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 按分隔符转成字符串列表
     * 
     * @param str
     * @param separatorChars
     * @return
     */
    public static List<String> splitToList(String str, String separatorChars) {
        String[] array = split(str, separatorChars);
        return Arrays.asList(array);
    }

    /**
     * 获取到文件名的后缀
     *
     * @param fileName
     * @return
     */
    public static String parseMaterialType(String fileName) {
        if (!StringUtils.isBlank(fileName)) {
            int beginIndex = fileName.lastIndexOf(".");
            return fileName.substring(beginIndex + 1);
        }
        return null;
    }

}
