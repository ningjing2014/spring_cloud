package com.ln.xproject.job.constants;

public enum JobMachineStatus {

    APPL_TO_RECALL("applToRecall", "进件回调业务"),

    APPL_TO_AUDIT("applToAudit", "进件审核"),

    APPL_TO_PUSH("applToPush", "进件推标到WE"),

    APPL_TO_QUERY("applToQuery", "进件状态到支付查询"),;
    private String status;
    private String desc;

    private JobMachineStatus(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static JobMachineStatus getEnum(String val) {
        for (JobMachineStatus status : JobMachineStatus.values()) {
            if (status.getStatus().equals(val)) {
                return status;
            }
        }
        return null;
    }

    public static String getMessage(String val) {
        for (JobMachineStatus status : JobMachineStatus.values()) {
            if (status.getStatus().equals(val)) {
                return status.getDesc();
            }
        }
        return null;
    }

}
