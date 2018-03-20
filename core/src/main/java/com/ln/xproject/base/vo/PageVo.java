package com.ln.xproject.base.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ning on 2/13/17.
 */
public class PageVo<T> extends HashMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 1266795568475950859L;

    private static final String LIST_KEY = "list";
    private static final String TOTAL_KEY = "recordsTotal";

    public PageVo(List<T> list, long recordsTotal) {
        super();

        super.put(LIST_KEY, list);
        super.put(TOTAL_KEY, recordsTotal);
    }

}
