package com.ln.xproject.util;

import java.util.HashMap;
import java.util.Map;

public class MapUtils extends org.apache.commons.collections.MapUtils {
    /**
     * 简易单值kv map
     * 
     * @param k
     * @param v
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> of(K k, V v) {
        Map<K, V> map = new HashMap<K, V>();
        map.put(k, v);
        return map;
    }
}
