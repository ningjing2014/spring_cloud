package com.ln.xproject.job.constants;

public enum JobType {

                     COMMON_JOB(0, "普通任务"),
                     TIME_JOB(1, "定时任务");
    private int type;
    private String desc;

    private JobType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static JobType getEnum(int type) {
        for (JobType jobType : JobType.values()) {
            if (jobType.getType() == type) {
                return jobType;
            }
        }
        return null;
    }
}
