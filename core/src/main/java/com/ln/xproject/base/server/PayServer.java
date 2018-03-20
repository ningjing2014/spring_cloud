package com.ln.xproject.base.server;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ln.xproject.base.spring.SpringContext;
import com.ln.xproject.util.MD5Utils;
import com.ln.xproject.util.StringUtils;

/**
 * 支付系统
 *
 * @author ln
 */
@Component
public class PayServer {

    // 支付系统server
    @Value("${server.pay.url}")
    private String serverUrl;

    // 支付系统签名salt
    @Value("${server.pay.verify.call.salt.key}")
    private String accountCallSaltKey;

    @Value("${server.pay.call.verify.salt.key}")
    private String callAccountSalt;

    @PostConstruct
    public void fillServerSign() {
        ServerConfigHolder.fillServerSignSalt(PartnerCode.PAY.getCode(), callAccountSalt);
    }

    // 支付系统代付
    public String getServerPay() {
        // SERVER_PAY
        return serverUrl + "/pay/pay";
    }

    // 支付系统代付查询
    public String getServerPayQuery() {
        // SERVER_PAY_QUERY
        return serverUrl + "/pay/payQuery";
    }

    public static enum BussinessStatus {

        SUCCESS("0", "成功"), GENERAL_DEAL_SUCCESS("00", "处理成功"), GENERAL_DEAL_FAIL("01", "处理失败"), GENERAL_DEALING("02",
                "处理中"), ERROR_REPEAT("100003", "订单已存在"), CASH_HAS_SUCCESS("101302", "上笔提现目前已成功，不允许重复提现"),
        // 信息已存在，无法开户（用户在共同使用的支付主体账号的其他业务开户）
        ERROR_INFO_EXISTS("101301", "该用户已开户，身份证号已存在");
        private String status;
        private String message;

        private BussinessStatus(String status, String message) {
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

        public static BussinessStatus getEnum(String status) {
            for (BussinessStatus code : BussinessStatus.values()) {
                if (code.getStatus().equals(status)) {
                    return code;
                }
            }
            return null;
        }
    }

    public static String getSignOfPay(Map<String, String> paramMap) {
        TreeMap<String, String> map = new TreeMap<String, String>();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            if (StringUtils.isNotBlank(entry.getValue())) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        StringBuffer sbuffer = new StringBuffer(getStaticAccountCallSaltKey());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sbuffer.append(entry.getValue());
        }
        sbuffer.append(getStaticAccountCallSaltKey());
        return MD5Utils.md5Str(sbuffer.toString());
    }

    private static String getStaticAccountCallSaltKey() {
        PayServer payServer = SpringContext.getApplicationContext().getBean("payServer", PayServer.class);
        return payServer.getAccountCallSaltKey();
    }

    public String getAccountCallSaltKey() {
        return accountCallSaltKey;
    }

}
