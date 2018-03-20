package com.ln.xproject.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CollectionUtils extends org.apache.commons.collections.CollectionUtils {

    /**
     * 数组转set
     * 
     * @param tArray
     * @param <T>
     * @return
     */
    public static <T extends Object> Set<T> Array2Set(T[] tArray) {
        Set<T> tSet = new HashSet<T>(Arrays.asList(tArray));
        return tSet;
    }

}
