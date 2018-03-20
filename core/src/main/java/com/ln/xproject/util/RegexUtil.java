package com.ln.xproject.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

/**
 * 通用正则工具类
 * 
 * @author xunz 2015/10/21 14:40
 */
@Slf4j
public class RegexUtil {
    /**
     * 校验给定的密码是否为有效密码
     * <ul>
     * <li>密码由8-16位数字和字母（区分大小写）组成,</li>
     * <li>密码组合必须至少有一个数字和一个字母。</li>
     * <li>特殊字符可选。数字1-8上面的特殊符号、英文“.”及“_”可以备选</li>
     * </ul>
     * 
     * @param password
     *            待校验的密码
     * @return 给定的密码是否为有效的密码，true:有效；false:无效
     */
    public static boolean isValidPassword(String password) {
        String regex = "^(?![\\d!@#$%^&*._]{8,16}$|[a-zA-Z!@#$%^&*._]{8,16}$)[a-zA-Z\\d!@#$%^&*._]{8,16}$";
        return Pattern.matches(regex, password);
    }

    /**
     * 校验邮箱合法性
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        Pattern emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher matcher = emailPattern.matcher(email);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

}
