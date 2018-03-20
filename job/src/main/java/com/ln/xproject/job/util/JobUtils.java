package com.ln.xproject.job.util;

import com.ln.xproject.job.base.BaseJob;

public class JobUtils {

    /** Job运行Key前缀 */
    public static final String JOB_RUNNING_KEY_PREFIX = "JOB_RUNNING_";
    /** Job运行Key超时时间 */
    public static final long JOB_RUNNING_KEY_EXPIRED = 7 * 24 * 60 * 60;

    /**
     * 获取Job运行的Key
     * 
     * @param clazz
     * @return
     */
    public static <T extends BaseJob> String getJobRunningKey(Class<T> clazz) {
        return JOB_RUNNING_KEY_PREFIX + clazz.getSimpleName();
    }

}
