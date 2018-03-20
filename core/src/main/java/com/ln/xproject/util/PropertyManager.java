package com.ln.xproject.util;

import java.util.ResourceBundle;

/**
 * 提供一个读取不同resource文件的类,可以更改一下,明白的实现多例
 */
public class PropertyManager {
    /**
     * 读取指定为rb.properties文件
     * @param rb property 文件，文件名为 rb.properties
     * @param key 
     * @return value
     */
    public static String getRB(String rb, String key) {
        try {
            return ResourceBundle.getBundle(rb).getString(key);
        } catch (Exception e) {
            throw new RuntimeException('!' + rb + ":" + key + '!');
        }
    }
}
