package com.ln.xproject.system.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ln.xproject.base.controller.BaseController;
import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.exception.ExceptionUtils;
import com.ln.xproject.base.vo.JsonResultVo;
import com.ln.xproject.system.service.SysUserService;
import com.ln.xproject.system.vo.LoginRstVo;
import com.ln.xproject.util.IPUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录页相关操作Controller
 *
 * @author xunz 2015/8/27 14:46
 */
@RestController
@Slf4j
public class LoginController extends BaseController {

    @Resource
    private SysUserService sysUserService;

    /**
     * 登录
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public JsonResultVo login(@RequestParam(required = true, value = "username") String username,
            @RequestParam(required = true, value = "password") String password, 
            @RequestParam(required = true, value = "validateCode") String validateCode,
            HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String ip = IPUtils.getIPAddress(request);
        JsonResultVo json = JsonResultVo.success();
        LoginRstVo rstVo = sysUserService.login(username, password, validateCode, ip);
        return json.addData("perms", rstVo);
    }

    /**
     * 退出
     */
    @RequestMapping("/logout")
    public JsonResultVo logout(HttpServletRequest request, HttpServletResponse response) {
        sysUserService.logout();
        return JsonResultVo.success();
    }

    /**
     * 登录检查校验
     */
    @RequestMapping("/loginCheck")
    public JsonResultVo loginCheck(HttpServletRequest request, HttpServletResponse response) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            throw ExceptionUtils.getServiceException(CodeConstants.C_10111000);
        }
        return JsonResultVo.success();
    }

    /**
     * 需要登录
     */
    @RequestMapping("/noLogin")
    public JsonResultVo requestLogin(HttpServletRequest request, HttpServletResponse response) {
        return JsonResultVo.error(CodeConstants.C_10111000);
    }

    /**
     * 需要授权
     */
    @RequestMapping("/noAuth")
    public JsonResultVo requestAuth(HttpServletRequest request, HttpServletResponse response) {
        return JsonResultVo.error(CodeConstants.C_10111001);
    }

}
