package com.ln.xproject.system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ln.xproject.base.controller.BaseController;
import com.ln.xproject.base.vo.JsonResultVo;
import com.ln.xproject.system.service.SysPermissionService;
import com.ln.xproject.system.vo.PermTreeVo;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by ning on 2/4/17.
 */
@RestController
@RequestMapping("/sysPermission")
@Slf4j
public class SysPermissionController extends BaseController {
    @Autowired
    private SysPermissionService sysPermissionService;

    /**
     * 获取权限树
     */
    @RequestMapping(value = "/getPermissionTree", method = { RequestMethod.POST, RequestMethod.GET })
    public JsonResultVo getPermissionTree() {
        List<PermTreeVo> permTree = this.sysPermissionService.getPermTree();
        return JsonResultVo.success().addData("permTree", permTree);
    }

    /**
     * 获取角色权限
     */
    @RequestMapping(value = "/getPermissionByRole", method = { RequestMethod.POST, RequestMethod.GET })
    public JsonResultVo getPermissionByUser(@RequestParam(required = true, value = "roleId") Long... roleId) {
        List<Long> perms = this.sysPermissionService.getSysPermissionByRole(roleId);
        return JsonResultVo.success().addData("perms", perms);
    }

}
