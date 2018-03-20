package com.ln.xproject.base.server;

public enum PartnerCode {
    PAY("PAY", "server/pay"), VERIFY("VERIFY", "server/verify"),;

    private String code;
    private String config;

    private PartnerCode(String code, String config) {
        this.code = code;
        this.config = config;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public static PartnerCode getEnum(String code) {
        for (PartnerCode product : PartnerCode.values()) {
            if (product.getCode().equals(code)) {
                return product;
            }
        }
        return null;
    }

}
