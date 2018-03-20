package com.ln.xproject.system.vo;

import lombok.Data;

/**
 * Created by ning on 2/4/17.
 */
@Data
public class MenuPermVo {

    public MenuPermVo(Long permId, String name, String url, String permission, String description, String type) {
        this.permId = permId;
        this.name = name;
        this.url = url;
        this.permission = permission;
        this.description = description;
        this.type = type;
    }

    /** 权限主键 */
    private Long permId;

    /** 权限名称 */
    private String name;

    /** url地址 */
    private String url;

    /** 权限字符串,如：user:view */
    private String permission;

    /** 权限描述 */
    private String description;

    /** 权限类型 */
    private String type;

}
