package com.ln.xproject.system.vo;

import java.util.List;

import lombok.Data;

/**
 * Created by ning on 2/4/17.
 */
@Data
public class PermTreeVo {
    public PermTreeVo(Long id, String name, String url, String permission, String description, String type,
            Long parentId, Integer orderNum) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.permission = permission;
        this.description = description;
        this.type = type;
        this.parentId = parentId;
        this.orderNum = orderNum;
    }

    /** 业务主键 */
    private Long id;

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

    /** 父级Id */
    Long parentId;

    /** 排序序号 */
    Integer orderNum;

    /** 权限列表 */
    List<PermTreeVo> childPerms;

}
