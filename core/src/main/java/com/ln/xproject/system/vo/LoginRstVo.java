package com.ln.xproject.system.vo;

import java.util.List;
import java.util.Set;

import lombok.Data;

/**
 * Created by ning on 2/6/17.
 */
@Data
public class LoginRstVo {

    /** 是否是管理员 */
    private Boolean isAdmin;

    /** 如果不是管理员，所有的权限列表 */
    private List<MenuPermVo> menuPerms;

    /** 数据权限控制 */
    private Set<String> dataPerms;

    /** 是否需要重置密码 */
    private Boolean isNeedResetPwd;

    /** 邮箱 */
    private String email;

    /** 真实姓名 */
    private String realName;

}
