package com.ln.xproject.base.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 账务系统代理
 *
 * @author ln
 */
@Component
public class VerifyServer {

    // 支付系统server
    @Value("${server.url}")
    private String serverUrl;

    // 支付系统代付
    public String getServerPayRecall() {
        // SERVER_PAY
        return getServerUrl() + "/verify/recall/pay/pay";
    }

    private String getServerUrl() {
        return serverUrl;
    }

}
