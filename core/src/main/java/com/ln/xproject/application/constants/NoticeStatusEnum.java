package com.ln.xproject.application.constants;

/**
 * Created by ning on 2/17/17.
 */
public enum NoticeStatusEnum {
    WAIT_PROCESS {
        @Override
        public String toString() {
            return "等待处理";
        }
    },

    PROCESS_SUCCESS {
        @Override
        public String toString() {
            return "处理成功";
        }
    },
    PROCESS_FAIL {
        @Override
        public String toString() {
            return "处理失败";
        }
    };
    @Override
    public abstract String toString();
}
