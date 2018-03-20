package com.ln.xproject.base.server;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ln.xproject.application.constants.ApplicationChannel;

@Component
public class UcreditServer {
    // 友信请求信审的签名盐
    @Value("${ucredit.call.sign.salt}")
    private String salt;

    // 友信征信系统服务地址
    @Value("${credit.server.url}")
    private String creditServerUrl;

    // 请求来源
    @Value("${call.credit.system.source}")
    public String systemSource;

    // token请求用户名
    @Value("${call.credit.token.user}")
    public String tokenUser;

    // token请求密码
    @Value("${call.credit.token.password}")
    public String tokenPassword;

    @Value("${call.credit.callback.user}")
    public String callBackUser;

    @Value("${call.credit.callback.password}")
    private String callBackPassword;

    @PostConstruct
    public void fillServerSalt() {
        ServerConfigHolder.fillServerSignSalt(ApplicationChannel.UCREDIT.name(), salt);
    }

    // 查询token的url
    public String getTokenQueryUrl() {
        String tokenUrlFormat = creditServerUrl + "/initauth/getToken?systemUserName=%s&systemUserPassword=%s";
        return String.format(tokenUrlFormat, this.tokenUser, this.tokenPassword);
    }

    // 查询征信报告信息的url
    public String getReportQueryUrl() {
        return creditServerUrl + "/auth/findRrdPbcInfoByIdNo?systemResource=" + this.systemSource
                + "&systemResourceId=%s&idNo=%s";
    }

    public String getCallBackUser() {
        return callBackUser;
    }

    public String getCallBackPassword() {
        return callBackPassword;
    }

    public static enum ResponseStatus {
        SUCCESS("00", "成功"), BUSINESS_FAIL("10", "业务处理失败"), AUTHENTICATE_FAIL("11", "权限认证失败");

        private String status;
        private String message;

        private ResponseStatus(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
