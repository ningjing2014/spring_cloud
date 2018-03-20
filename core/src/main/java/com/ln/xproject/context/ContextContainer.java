package com.ln.xproject.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationContext;

public class ContextContainer {
    private static Map<String, String> serverSignMap = new ConcurrentHashMap<String, String>();

    private static ApplicationContext ac = null;

    public static Object getBean(String beanName) {
        return ac.getBean(beanName);
    }

    public static void setAc(ApplicationContext ac) {
        ContextContainer.ac = ac;
    }

    public static ApplicationContext getAc() {
        return ac;
    }

    public static Map<String, String> getServerSignMap() {
        return serverSignMap;
    }
    
    public static void fillServerSign(String partnerCode, String sign) {
        serverSignMap.put(partnerCode, sign);
    }
}
