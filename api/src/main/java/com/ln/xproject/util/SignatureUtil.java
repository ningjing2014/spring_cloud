package com.ln.xproject.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SignatureUtil {

    public static String signature(TreeMap<String, String> params, String secret) {
        List<String> list = new ArrayList<String>();
        Iterator<Entry<String, String>> iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = (Entry<String, String>) iter.next();
            String key = entry.getKey();
            String val = entry.getValue();
            if (StringUtils.isNotBlank(val)) {
                list.add(key + "=" + val);
            }
        }
        StringBuilder buf = new StringBuilder();
        for (String s : list) {
            buf.append(s).append("&");
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        buf.append(secret);// (3)append secret key
        String calMd5 = MD5Util.md5(buf.toString());
        // 精简日志
        log.info("签名串:{}, calSign:{}", buf.toString(), calMd5);
        return calMd5;
    }

    public static void main(String[] args) {
        String secret = "c60de7822a0cee7d6bd152bc9c985000"; // secret key
                                                            // (32bit)
        TreeMap<String, String> map = new TreeMap<String, String>();
        map.put("key2", "1");
        map.put("key1", "2");
        String str = SignatureUtil.signature(map, secret);
        System.out.println(str);
        str = SignatureUtil.signature(map, secret);
        System.out.println(str);
        str = SignatureUtil.signature(map, secret);
        System.out.println(str);
        str = SignatureUtil.signature(map, secret);
        System.out.println(str);
        str = SignatureUtil.signature(map, secret);
        System.out.println(str);
    }

}
