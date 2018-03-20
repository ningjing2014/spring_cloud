package com.ln.xproject.system.service;

import java.util.List;
import java.util.Map;

import com.ln.xproject.base.constants.ValidStatus;
import com.ln.xproject.base.service.BaseService;
import com.ln.xproject.base.vo.PageVo;
import com.ln.xproject.system.constants.SearchUserType;
import com.ln.xproject.system.model.SysUser;
import com.ln.xproject.system.vo.LoginRstVo;
import com.ln.xproject.system.vo.UserListVo;

public interface SysUserService extends BaseService<SysUser> {
    /**
     * 根据email获取用户信息 ，如果不存在抛出异常
     *
     * @param email
     * @return
     */
    public SysUser loadByEmail(String email);

    /**
     * 根据email获取用户信息
     *
     * @param email
     * @return
     */
    SysUser getByEmail(String email);

    /**
     * 登录
     *
     * @param email
     *            登录邮箱
     * @param password
     *            登录密码
     * @param validateCode
     *            验证码
     * @param loginIP
     *            登录的ip地址
     * @return
     */
    LoginRstVo login(String email, String password, String validateCode, String loginIP);

    /**
     * 登出
     */
    void logout();

    /**
     * 踢出
     * 
     * @param userId
     */
    void kickOutUser(Long userId);

    /**
     * 添加一个用户
     * 
     * @param email
     * @param realName
     * @return
     */
    void addSysUser(String email, String realName);

    /**
     * 删除用户
     * 
     * @param userId
     */
    void updateUserStatus(Long userId, ValidStatus status);

    /**
     * 查询用户列表
     * 
     * @param type
     * @param value
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageVo<UserListVo> searchUserList(SearchUserType type, String value, Integer pageNum, Integer pageSize);

    /**
     * 查询某种角色的用户信息
     * @param roleId
     * @return
     */
    List<Map> getUserByRoleId(Long roleId);
}
