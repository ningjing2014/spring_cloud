package com.ln.xproject.base.interceptor;

import java.lang.reflect.Method;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ln.xproject.api.service.SignService;
import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.exception.ServiceException;
import com.ln.xproject.base.vo.ApiBaseRequestVo;
import com.ln.xproject.base.vo.JsonResultVo;
import com.ln.xproject.util.JsonUtils;
import com.ln.xproject.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Service
public class SignAspect {

    @Value("${is.sign.white.abled}")
    private Boolean isSignWhiteAbled;

    @Value("${sign.white.list}")
    private String signWhiteListStr;

    @Autowired
    private SignService signService;

    @SuppressWarnings("unchecked")
    @Around("controllerMethod() && requestMappingMethod()")
    public <T extends ApiBaseRequestVo> Object validSign(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        if (null == args || args.length != 1) {
            return joinPoint.proceed();
        }
        Object obj = joinPoint.getArgs()[0];

        RequestMapping classRequestMapping = joinPoint.getTarget().getClass().getAnnotation(RequestMapping.class);

        String classPath = "";
        if (null != classRequestMapping) {
            classPath = classRequestMapping.value()[0];
        }
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        RequestMapping methodRequestMapping = (RequestMapping) method.getAnnotation(RequestMapping.class);
        String methodPath = methodRequestMapping.value()[0];

        String path = classPath + methodPath;

        log.info("请求url:{}, params:{}", path, JsonUtils.toJson(obj));

        if (!isNeedSign(obj)) {
            log.info("not need valid sign");
            return joinPoint.proceed();
        }
        // 白名单
        if (hitWhiteList(path)) {
            log.info("hit white list, not need valid sign");
            return joinPoint.proceed();
        }

        try {
            ApiBaseRequestVo baseVo = (ApiBaseRequestVo) obj;
            Assert.notBlank(baseVo.getServiceVersion(), "版本号");
            Assert.notBlank(baseVo.getRequestTime(), "请求时间");
            Assert.notBlank(baseVo.getPartner(), "数据来源渠道");
            Assert.notBlank(baseVo.getSign(), "签名");

            T vo = (T) obj;

            // 校验签名
            if (signService.verify(vo)) {
                return joinPoint.proceed();
            } else {
                return JsonResultVo.error(CodeConstants.C_10121003);
            }
        } catch (ServiceException e) {
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return JsonResultVo.error();
        }
    }

    /*
     * 是否命中白名单绕过验签
     */
    private boolean hitWhiteList(String path) {
        if (!isSignWhiteAbled || StringUtils.isBlank(signWhiteListStr)) {
            return false;
        }
        List<String> whiteList = StringUtils.splitToList(signWhiteListStr, "|");

        if (CollectionUtils.isEmpty(whiteList)) {
            return false;
        }
        return whiteList.contains(path);
    }

    /*
     * 判断是否需要验签
     */
    private boolean isNeedSign(Object obj) {
        return (obj instanceof ApiBaseRequestVo || ApiBaseRequestVo.class.isAssignableFrom(obj.getClass()));
    }

    /**
     * RequestMapping切点
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMappingMethod() {

    }

    /**
     * Controller方法切点
     */
    @Pointcut("execution(* com.ln.xproject.controller..*.*(..))")
    public void controllerMethod() {

    }
}
