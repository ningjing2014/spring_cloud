package com.ln.xproject.job.constants;

public enum DbTableStatus {
                           FAIL(-1, "处理失败"),
                           INIT(0, "待处理"),
                           DEALING(1, "处理中"),
                           SUCCESS(2, "处理成功"),
                           NOTICE_MANUAL(3, "通知人工干预")
 ;
    private int status;
    private String desc;

    private DbTableStatus(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static DbTableStatus getEnum(int val) {
        for (DbTableStatus status : DbTableStatus.values()) {
            if (status.getStatus() == val) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据status 获取message
     * @param val
     * @return
     */
    public static String getMessage(int val) {
        for (DbTableStatus status : DbTableStatus.values()) {
            if (status.getStatus() == val) {
                return status.getDesc();
            }
        }
        return null;
    }

    /**
     * 根据message获取Status
     */
    public static Integer getStatus(String message){
        for (DbTableStatus status : DbTableStatus.values()) {
            if (status.getDesc().equals(message)) {
                return status.getStatus();
            }
        }
        return null;
    }

}
