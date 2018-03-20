package com.ln.xproject.util;

public class RandomUtils extends org.apache.commons.lang3.RandomUtils {

    /**
     * 生成指定长度的随机数字序列
     * 
     * @param length
     * @return
     */
    public static String getFixLengthString(int length) {

        if (length < 0) {
            length = 0;
        }

        double tmp = nextDouble(Math.pow(10, length - 1), Math.pow(10, length));

        return String.valueOf(tmp).substring(0, length);
    }

    public static void main(String[] args) {
        System.out.println(getFixLengthString(-1));
    }
}
