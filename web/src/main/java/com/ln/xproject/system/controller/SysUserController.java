package com.ln.xproject.system.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ln.xproject.base.controller.BaseController;
import com.ln.xproject.util.UserUtils;
import com.ln.xproject.base.constants.ValidStatus;
import com.ln.xproject.base.exception.ExceptionUtils;
import com.ln.xproject.base.exception.ServiceException;
import com.ln.xproject.base.vo.JsonResultVo;
import com.ln.xproject.base.vo.PageVo;
import com.ln.xproject.system.constants.SearchUserType;
import com.ln.xproject.system.service.SysUserChannelService;
import com.ln.xproject.system.service.SysUserSecurityService;
import com.ln.xproject.system.service.SysUserService;
import com.ln.xproject.system.vo.ChannelVo;
import com.ln.xproject.system.vo.UserListVo;
import com.ln.xproject.util.PassCheckUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录页相关操作Controller
 */
@RestController
@RequestMapping("/sysUser")
@Slf4j
public class SysUserController extends BaseController {

    private static final long AUDITOR_ROLE_ID = 3L;

    @Autowired
    private SysUserSecurityService sysUserSecurityService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserChannelService sysUserBusinessLineService;

    /**
     * 修改密码
     */
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    public JsonResultVo updatePassword(@RequestParam(required = true, value = "password") String password,
            @RequestParam(required = true, value = "newPassword") String newPassword, HttpServletRequest request,
            HttpServletResponse response) {
        if (PassCheckUtils.checkPassIsSimple(newPassword) || (newPassword != null && newPassword.length() > 16)) {
            throw ExceptionUtils.commonError("密码8-16位，且需要含有大小写字母、数字、特殊字符(@#$%^&*_)");
        }
        sysUserSecurityService.updatePassword(UserUtils.getCurrentUserId(), password, newPassword);
        // 修改密码后需要重新登录
        this.sysUserService.logout();
        return JsonResultVo.success();
    }

    /**
     * 重置密码
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public JsonResultVo resetPassword(@RequestParam(required = true, value = "userId") Long userId,
            HttpServletRequest request, HttpServletResponse response) {
        sysUserSecurityService.resetPassword(userId);
        return JsonResultVo.success();
    }

    /**
     * 忘记密码找回密码
     */
    @RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
    public JsonResultVo forgetPassword(@RequestParam(required = true, value = "realName") String realName,
            @RequestParam(required = true, value = "email") String email, HttpServletRequest request,
            HttpServletResponse response) {
        sysUserSecurityService.resetPassword(email, realName);
        return JsonResultVo.success();
    }

    /**
     * 添加用户
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JsonResultVo add(@RequestParam(required = true, value = "email") String email,
            @RequestParam(required = true, value = "realName") String realName, HttpServletRequest request,
            HttpServletResponse response) {
        sysUserService.addSysUser(email, realName);
        return JsonResultVo.success();
    }

    /**
     * 修改用户状态
     */
    @RequestMapping(value = "/updateUserStatus", method = RequestMethod.POST)
    public JsonResultVo updateUserStatus(@RequestParam(required = true, value = "userId") Long userId,
            @RequestParam(required = true, value = "status") ValidStatus status, HttpServletRequest request,
            HttpServletResponse response) {
        sysUserService.updateUserStatus(userId, status);
        return JsonResultVo.success();
    }

    /**
     * 踢出用户
     */
    @RequestMapping(value = "/kickOutUser", method = RequestMethod.POST)
    public JsonResultVo kickOutUser(@RequestParam(required = true, value = "userId") Long userId,
            HttpServletRequest request, HttpServletResponse response) {
        sysUserService.kickOutUser(userId);
        return JsonResultVo.success();
    }

    /**
     * 获取业务线列表
     */
    @RequestMapping(value = "/getBusinessChannel", method = { RequestMethod.POST, RequestMethod.GET })
    public JsonResultVo getBusinessChannel(HttpServletRequest request, HttpServletResponse response) {
        List<ChannelVo> list = sysUserBusinessLineService.getChannels();
        return JsonResultVo.success().addData("list", list);
    }

    /**
     * 获取用户列表
     */
    @RequestMapping(value = "/getUserList", method = RequestMethod.POST)
    public JsonResultVo getUserList(@RequestParam(required = true, value = "pageNum") Integer pageNum,
            @RequestParam(required = true, value = "pageSize") Integer pageSize,
            @RequestParam(required = true, value = "key") SearchUserType key,
            @RequestParam(required = true, value = "value") String value) {
        pageNum = this.getPageNum(pageNum);
        pageSize = this.getPageSize(pageSize);
        PageVo<UserListVo> list = sysUserService.searchUserList(key, value, pageNum, pageSize);
        return JsonResultVo.success().setData(list);
    }

    /**
     * 获取某种角色的用户
     */
    @RequestMapping(value = "/user/list", method = RequestMethod.POST)
    public JsonResultVo getUserList(@RequestParam(required = false, value = "roleId") Long roleId) {
        try {
            //roleId为空 默认为审核员
            if(roleId == null){
                roleId = AUDITOR_ROLE_ID;
            }
            List<Map> resultList = sysUserService.getUserByRoleId(roleId);
            return JsonResultVo.success().addData("list", resultList);
        } catch (ServiceException e) {
            log.error("查询失败:{}", e.getMessage(), e);
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询失败", e);
            return JsonResultVo.error();
        }
    }

}
