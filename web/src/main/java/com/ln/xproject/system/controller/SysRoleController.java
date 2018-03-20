package com.ln.xproject.system.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ln.xproject.base.controller.BaseController;
import com.ln.xproject.base.vo.JsonResultVo;
import com.ln.xproject.system.service.SysRoleService;
import com.ln.xproject.system.vo.RoleVo;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录页相关操作Controller
 * 
 * @author xunz 2015/8/27 14:46
 */
@RestController
@RequestMapping("/sysRole")
@Slf4j
public class SysRoleController extends BaseController {

    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 添加角色
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public JsonResultVo add(@RequestParam(required = true, value = "name") String name,
            @RequestParam(required = true, value = "description") String description,
            @RequestParam(required = true, value = "orderNum") Integer orderNum, HttpServletRequest request,
            HttpServletResponse response) {
        this.sysRoleService.add(name, description, orderNum);
        return JsonResultVo.success();
    }

    /**
     * 删除角色
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public JsonResultVo delete(@RequestParam(required = true, value = "roleId") Long roleId, HttpServletRequest request,
            HttpServletResponse response) {
        sysRoleService.delete(roleId);
        return JsonResultVo.success();
    }

    /**
     * 更新角色
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public JsonResultVo add(@RequestParam(required = true, value = "roleId") Long roleId,
            @RequestParam(required = true, value = "name") String name,
            @RequestParam(required = true, value = "description") String description,
            @RequestParam(required = true, value = "orderNum") Integer orderNum, HttpServletRequest request,
            HttpServletResponse response) {
        this.sysRoleService.update(roleId, name, description, orderNum);
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
