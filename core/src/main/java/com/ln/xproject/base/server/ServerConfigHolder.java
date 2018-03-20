package com.ln.xproject.base.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerConfigHolder {
    private static Map<String, String> serverSignSaltMap = new ConcurrentHashMap<String, String>();

    public static Map<String, String> getServerSignSaltMap() {
        return serverSignSaltMap;
    }

    public static void fillServerSignSalt(String partnerCode, String sign) {
        serverSignSaltMap.put(partnerCode, sign);
    }
}
