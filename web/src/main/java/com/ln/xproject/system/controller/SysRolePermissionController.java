package com.ln.xproject.system.controller;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ln.xproject.base.controller.BaseController;
import com.ln.xproject.base.vo.JsonResultVo;
import com.ln.xproject.system.service.SysRolePermissionService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by ning on 2/4/17.
 */
@RestController
@RequestMapping("/sysRolePermission")
@Slf4j
public class SysRolePermissionController extends BaseController {
    @Autowired
    private SysRolePermissionService sysRolePermissionService;

    /**
     * 设置角色权限
     */
    @RequestMapping(value = "/setRolePermission", method = RequestMethod.POST)
    public JsonResultVo setRolePermission(@RequestParam(required = true, value = "roleId") Long roleId,
            @RequestParam(required = true, value = "permissionIds") Set<Long> permissionIds,
            HttpServletResponse response) {
        this.sysRolePermissionService.setRolePermission(roleId, permissionIds);
        return JsonResultVo.success();
    }

}
