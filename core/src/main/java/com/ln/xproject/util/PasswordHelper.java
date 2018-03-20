package com.ln.xproject.util;

import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

import com.ln.xproject.system.model.SysUserSecurity;

/**
 * 密码工具类
 * 
 * @author xunz 2015/8/26 17:38
 */
public class PasswordHelper {
    /**
     * 加密方式
     */
    private static String algorithmName = "md5";

    /**
     * 加密次数
     */
    private static int hashIterations = 1;

    /**
     * 给用户密码进行加密，同时记录密码盐。 Mutates user in-place.
     *
     * @param sysUserSecurity
     */
    public static void encryptPassword(SysUserSecurity sysUserSecurity) {
        // 密码盐
        String passwordSalt = RandomStringUtils.randomAlphanumeric(32);
        sysUserSecurity.setPasswordSalt(passwordSalt);

        String newPassword = new SimpleHash(algorithmName, sysUserSecurity.getPassword(),
                ByteSource.Util.bytes(sysUserSecurity.getPasswordSalt()), hashIterations).toHex();

        sysUserSecurity.setPassword(newPassword);
    }

    /**
     * 给用户密码进行加密，使用指定的密码盐。
     * 
     * @param password
     *            密码
     * @param passwordSalt
     *            密码盐
     * @return 加密后的密码
     */
    public static String encryptPassword(String password, String passwordSalt) {
        return new SimpleHash(algorithmName, password, ByteSource.Util.bytes(passwordSalt), hashIterations).toHex();
    }

    public static void main(String[] args) {
        PasswordHelper ph = new PasswordHelper();
        SysUserSecurity user = new SysUserSecurity();
        user.setPassword("123456");
        ph.encryptPassword(user);
    }

    public static String generateNumberAndLetterValidCode(int length) {
        Random r = new Random(System.currentTimeMillis());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            // int n = r.nextInt(36); //author 周纠平20150608 ,修改bug 不能生成小写字母
            int n = r.nextInt(62);
            char c = (char) (n >= 36 ? n + 61 : (n >= 10 ? n + 55 : n + 48));
            sb.append(c);
        }
        return sb.toString();
    }
}
