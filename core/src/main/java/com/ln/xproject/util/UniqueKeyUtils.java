package com.ln.xproject.util;

import com.ln.xproject.system.constants.ChannelType;

import java.util.Date;
import java.util.UUID;

public class UniqueKeyUtils {

    public static String uniqueKey() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    public static String generate( ChannelType channel){
        return  DateUtils.format(new Date(), "yyMMddHHmmss") + channel.getSerialNo()
                + RandomUtils.getFixLengthString(6);
    }

}
