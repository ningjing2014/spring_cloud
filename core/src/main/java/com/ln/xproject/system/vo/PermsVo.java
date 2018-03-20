package com.ln.xproject.system.vo;

import lombok.Data;

/**
 * Created by ning on 1/18/17.
 */
@Data
public class PermsVo {

    public PermsVo(String url, String permission) {
        this.url = url;
        this.permission = permission;
    }

    /** url地址 */
    private String url;

    /** 权限字符串,如：user:view */
    private String permission;
}
