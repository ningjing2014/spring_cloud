package com.ln.xproject.configuration.web.log;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ln.xproject.util.IPUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by ning on 2/26/17.
 */
@Component
@Slf4j
public class LogInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("requestStartTime", startTime);
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        log.info("请求ip:{},请求路径:{}, 请求参数:{}", IPUtils.getIPAddress(request), request.getServletPath(),
                this.getMapStringVal(getRequestMap(request)));

        return true;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        long startTime = (Long) request.getAttribute("requestStartTime");

        long endTime = System.currentTimeMillis();

        long executeTime = endTime - startTime;

        // log it
        if (executeTime > 1000) {
            log.info("[" + method.getDeclaringClass().getName() + "." + method.getName() + "] 执行耗时 : " + executeTime
                    + "ms");
        } else {
            log.info("[" + method.getDeclaringClass().getSimpleName() + "." + method.getName() + "] 执行耗时 : "
                    + executeTime + "ms");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getRequestMap(javax.servlet.http.HttpServletRequest request) {
        Enumeration<String> names = request.getParameterNames();
        Map<String, String> params = new HashMap<String, String>();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String value = request.getParameter(name);
            params.put(name, value);
        }
        return params;
    }

    public static String getMapStringVal(Map<String, String> map) {
        StringBuilder sbuf = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sbuf.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
            }
        } catch (Exception e) {
        }
        return sbuf.toString();
    }

}