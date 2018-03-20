package com.ln.xproject.application.constants;

/**
 * 进件推标状态
 *
 * @author ln
 */
public enum ApplicationPushStatus {
    TO_PUSH() {
        @Override
        public String toString() {
            return "待推标";
        }
    },
    SUCCESS() {
        @Override
        public String toString() {
            return "推标成功";
        }
    },
    FAILED() {
        @Override
        public String toString() {
            return "推标失败";
        }
    };

    public abstract String toString();
}
