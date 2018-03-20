package com.ln.xproject.shiro;

import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.springframework.beans.factory.annotation.Value;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@EqualsAndHashCode(callSuper=false)
public class SqlInjectAndXssFilter extends AdviceFilter {

    @Value("${sql_inject_filter_str}")
    private String sqlInjectFilterStr;
    @Value("${xss_pattern}")
    private String xssPattern;
    @Value("${xss_pattern2}")
    private String xssPattern2;
    
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response)
            throws Exception {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            // 对参数进行拦截,如果中有特殊字符，则返回结束，否则，继续执行
            Enumeration<String> names = httpRequest.getParameterNames();
            boolean flag = false;
            StringBuffer errorMsg = new StringBuffer();
            long beginTime = System.currentTimeMillis();// 开始时间
            while (names.hasMoreElements()) {
                String paramName = names.nextElement();
                String paramValue = StringUtils.trimToEmpty(httpRequest.getParameter(paramName));
                if (StringUtils.isNotEmpty(paramValue)) {
                    try {
                        paramValue = URLDecoder.decode(paramValue, "utf-8").toLowerCase();// 1,解码
                    } catch (Exception e) {
                        log.error("字符串不支持decode解码--->>" + paramValue, e);
                    }
                    paramValue = paramValue.toLowerCase();// 统一转为小写
                    // 2统一转为小写
                    // 1，查看参数中是否有特殊字符是否要进行过滤2， xss攻击过滤
                    if (sqlValidate(paramValue) || xssValidate(paramValue, xssPattern)
                            || xssValidate(paramValue, xssPattern2)) {
                        // 拼接有问题的错误字符，用于打印日志
                        errorMsg.append("paramName=").append(paramName).append("--->>paramValue=").append(paramValue);
                        flag = true;
                        break;
                    }
                }
            }
            // 结束时间
            long endTime = System.currentTimeMillis();
            log.info("信息[SqlInjectAndXssInterceptor]拦截器执行耗时：" + (endTime - beginTime) / 1000f + "秒");
            // flag为true表示有特殊字符，返回错误信息
            if (flag) {
                log.warn("信息：[SqlInjectInterceptor ]调用" + httpRequest.getServletPath() + "方法参数存在特殊字符，参数为" + errorMsg);
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            log.error("", e);
            return false;
        }
    }

    /**
     * <pre>
     * 功能描述：校验参数是否有特殊字符
     * @param str 要进行校验的字符串
     * @return true 存在特殊字符       false不存在特殊字符
     */
    private boolean sqlValidate(String str) {
        if (StringUtils.isBlank(sqlInjectFilterStr)) {
            return false;
        }
        Pattern pattern = Pattern.compile(sqlInjectFilterStr);
        return pattern.matcher(str).find();
    }

    /**
     * @param str
     *            要进行校验的字符串
     * @param xssPattern
     *            正则表达式
     * @return true 表示匹配上 false 表示没匹配上
     */
    private boolean xssValidate(String str, String xssPattern) {
        if (StringUtils.isBlank(xssPattern)) {
            return false;
        }
        Pattern pattern = Pattern.compile(xssPattern);
        return pattern.matcher(str).find();
    }
}
