package com.ln.xproject.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 此类仅用于验证手机号、身份证号、银行卡号等
 */
public class ValidUtils {

    /**
     * 身份证号是否有效
     * 
     * @param idNo
     * @return
     */
    public static boolean validIdNo(String idNo) {
        if (StringUtils.isBlank(idNo)) {
            return false;
        }
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile(
                "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$");
        m = p.matcher(idNo);
        b = m.matches();
        if (!b) {
            return false;
        }
        if (idNo.length() == 18) {
            int[] w = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
            String code = "10X98765432";
            int sum = 0;
            for (int i = 0; i <= 16; i++) {
                sum += w[i] * (idNo.charAt(i) - 48);
            }
            char c = code.charAt(sum % 11);
            if (idNo.charAt(17) == c) {
                return true;
            }
            return false;
        }

        return true;
    }

}
