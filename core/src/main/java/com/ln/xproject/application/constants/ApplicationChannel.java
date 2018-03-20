package com.ln.xproject.application.constants;

/**
 * 进件渠道
 * 
 * @author taixin
 */
public enum ApplicationChannel {

    // TODO 暂时写成UCREDIT
    UCREDIT("WE", "UCREDIT") {
        @Override
        public String toString() {
            return "友信";
        }
    };

    public abstract String toString();

    private String payChannel;
    private String paySubject;

    ApplicationChannel(String payChannel, String paySubject) {
        this.payChannel = payChannel;
        this.paySubject = paySubject;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public String getPaySubject() {
        return paySubject;
    }
}
