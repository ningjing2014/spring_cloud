package com.ln.xproject.system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ln.xproject.base.vo.JsonResultVo;
import com.ln.xproject.system.service.SysRoleService;
import com.ln.xproject.system.service.SysUserRoleService;
import com.ln.xproject.system.vo.RoleVo;
import com.ln.xproject.system.vo.UserRoleAndChannelInfoVo;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sysUserRole")
@Slf4j
public class SysUserRoleController {

    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 查询用户角色/业务线信息
     */
    @RequestMapping(value = "/getUserRoleAndBusinessChannelInfo", method = { RequestMethod.POST, RequestMethod.GET })
    public JsonResultVo getUserRoleAndChannel(@RequestParam(required = true, value = "userId") Long userId) {
        UserRoleAndChannelInfoVo rst = sysUserRoleService.getUserRoleAndChannelInfo(userId);
        return JsonResultVo.success().addData("roles", rst.getRoles()).addData("businessLine", rst.getChannels());
    }

    /**
     * 设置用户角色/业务线信息
     */
    @RequestMapping(value = "/setUserRolesAndBusinessChannelInfo", method = RequestMethod.POST)
    public JsonResultVo setUserRolesAndChannel(@RequestParam Long userId, @RequestParam Long[] roleId,
            String[] businessChannel) {
        this.sysUserRoleService.setUserRolesAndChannelInfo(userId, roleId, businessChannel);
        return JsonResultVo.success();
    }

    /**
     * 获取角色列表
     */
    @RequestMapping(value = "/getRoleList")
    public JsonResultVo getRoleList() {
        List<RoleVo> roleList = this.sysRoleService.getRoleList();
        return JsonResultVo.success().addData("roles", roleList);
    }

}
