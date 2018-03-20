package com.ln.xproject.util;

import java.util.Date;
import java.util.UUID;

/**
 * 按业务生成流水号 yyMMdd + 32位UUID
 */
public class SerialNoGenerator {

    public static String generateSerialNo() {
        Date date = new Date();
        return DateUtils.format(date, DateUtils.DATE_PATTERN_YYMMDD)
                + UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

}
