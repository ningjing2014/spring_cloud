package com.ln.xproject.system.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * Created by ning on 2/13/17.
 */
@Data
public class UserListVo implements Serializable {

    private Long userId;
    /** 真实姓名 */
    private String realName;
    /** 邮件名称 */
    private String email;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date lastLoginTime;
    /** 状态 */
    private String status;
}
