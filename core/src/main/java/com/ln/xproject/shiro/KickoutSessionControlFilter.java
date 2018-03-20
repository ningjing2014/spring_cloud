package com.ln.xproject.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;

import com.ln.xproject.kaptcha.KaptchaConstants;
import com.ln.xproject.system.service.impl.SysUserServiceImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * 同一用户多次登录，前一次用户登录被踢出Filter
 */
@Slf4j
public class KickoutSessionControlFilter extends AccessControlFilter {

    public KickoutSessionControlFilter() {
        super();
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
            throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        if (!subject.isAuthenticated()) {
            // 如果非免支付系统或者没有登录，直接进行之后的流程
            return true;
        }
        Session session = subject.getSession();

        // 踢出用户
        if (session.getAttribute(SysUserServiceImpl.SESSION_KEY_KICKOUT) != null) {
            // 会话被踢出了
            try {
                session.removeAttribute(KaptchaConstants.KAPTCHA_SESSION_KEY);
                subject.logout();
            } catch (Exception e) { // 所有异常均可log记录一下即可
                log.error("踢出用户", e);
            }
            // 退出登录，继续执行，有autchc filter控制登录跳转
            return true;
        }

        return true;
    }

}
