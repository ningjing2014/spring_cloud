package com.ln.xproject.configuration.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.session.InvalidSessionException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONException;
import com.ln.xproject.util.ResponseUtils;
import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.exception.ServiceException;
import com.ln.xproject.base.vo.JsonResultVo;
import lombok.extern.log4j.Log4j;

/**
 * 异常统一处理
 * 
 * @author lining
 */
@ControllerAdvice
@Log4j
public class FinanceExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {

        String errorMsg = null;
        String code = CodeConstants.C_10101000.getCode();
        if (ex instanceof ServiceException) {
            code = ((ServiceException) ex).getCode();
            errorMsg = ex.getMessage();
            log.info("业务异常", ex);
        } else if (ex instanceof MissingServletRequestParameterException) {
            log.error("缺失必要参数异常", ex);
            Object param = ((MissingServletRequestParameterException) ex).getParameterName();
            errorMsg = String.format("缺失必要的参数[%s]", param);
        } else if (ex instanceof ConversionNotSupportedException) {
            log.error("参数不合法", ex);
            errorMsg = "参数不合法";
        } else if (ex instanceof JSONException) {
            log.error("json解析异常", ex);
            errorMsg = "json格式不正确";
        } else if (ex instanceof IllegalArgumentException) {
            log.error("参数不合法", ex);
            errorMsg = "参数不合法";
        } else if (ex instanceof UnknownAccountException) {
            code = CodeConstants.C_10111003.getCode();
            errorMsg = CodeConstants.C_10111003.getMessage();
            log.error("用户名密码错误", ex);
        } else if (ex instanceof LockedAccountException) {
            code = CodeConstants.C_10111004.getCode();
            errorMsg = CodeConstants.C_10111004.getMessage();
            log.error("用户已冻结", ex);
        } else if (ex instanceof DisabledAccountException) {
            code = CodeConstants.C_10111005.getCode();
            errorMsg = CodeConstants.C_10111005.getMessage();
            log.error("用户已删除", ex);
        } else if (ex instanceof IncorrectCredentialsException) {
            log.error("用户名密码错误", ex);
            code = CodeConstants.C_10111003.getCode();
            errorMsg = CodeConstants.C_10111003.getMessage();
        } else if (ex instanceof AuthenticationException) {
            log.error("未知错误，登录失败", ex);
            code = CodeConstants.C_10111006.getCode();
            errorMsg = CodeConstants.C_10111006.getMessage();
        } else if (ex instanceof UnauthorizedException) {
            log.error("没有权限", ex);
            code = CodeConstants.C_10111007.getCode();
            errorMsg = CodeConstants.C_10111007.getMessage();
        } else if (ex instanceof InvalidSessionException) {
            log.error("会话过期", ex);
            code = CodeConstants.C_10111008.getCode();
            errorMsg = CodeConstants.C_10111008.getMessage();
        } else {
            log.error("未知异常", ex);
            errorMsg = "未知异常";
        }

        JsonResultVo vo = JsonResultVo.error(code, errorMsg);
        ResponseUtils.printJson(response, vo);

        return new ModelAndView();
    }
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ModelAndView HttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String[] supportedMethods = ex.getSupportedMethods();
        if (supportedMethods != null) {
            response.setHeader("Allow",
                    StringUtils.arrayToDelimitedString(supportedMethods, ", "));
        }
        response.sendError(405, ex.getMessage());
        return new ModelAndView();
    }
}
