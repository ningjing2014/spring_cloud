package com.ln.xproject.util;

public class PassCheckUtils {

    /**
     * 密码：长度不小于8，且必须含有大写字母、小写字母、数字、特殊字符
     * 
     * @param pass
     * @return
     */
    public static boolean checkPassIsSimple(String pass) {
        pass = StringUtils.defaultIfBlank(pass, "");
        if (pass.length() < 8) {
            return true;
        }
        boolean findUpperChar = false;
        char ch = 'A';
        for (int i = 0; i < 26; i++) {
            if (StringUtils.indexOf(pass, (char)(ch + i)) >= 0) {
                findUpperChar = true;
                break;
            }
        }
        if (!findUpperChar) {
            return true;
        }
        boolean findLowerChar = false;
        ch = 'a';
        for (int i = 0; i < 26; i++) {
            if (StringUtils.indexOf(pass, (char)(ch + i)) >= 0) {
                findLowerChar = true;
                break;
            }
        }
        if (!findLowerChar) {
            return true;
        }
        boolean findDigital = false;
        ch = '0';
        for (int i = 0; i < 10; i++) {
            if (StringUtils.indexOf(pass, (char)(ch + i)) >= 0) {
                findDigital = true;
                break;
            }
        }
        if (!findDigital) {
            return true;
        }
        char[] chars = new char[]{'!','@','#','$','%','^','&','*','_'};
        boolean findSpecialChar = false;
        for (int i = 0; i < chars.length; i++) {
            if (StringUtils.indexOf(pass, chars[i]) >= 0) {
                findSpecialChar = true;
                break;
            }
        }
        if (!findSpecialChar) {
            return true;
        }
        return false;
    }
    
    public static void main(String[] args) {
        checkPassIsSimple("Aabcaaaaaaaaaaa1^");
    }
}
