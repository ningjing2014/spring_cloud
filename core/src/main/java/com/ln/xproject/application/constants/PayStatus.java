package com.ln.xproject.application.constants;

/**
 * 还款订单异步处理状态
 * 
 * @date 2015年9月23日
 * @author taixin
 */
public enum PayStatus {
    /**
     * 等待处理
     */
    WAIT_PROCESS() {
        @Override
        public String toString() {
            return "等待处理";
        }
    },
    /**
     * 处理中
     */
    PROCESSING() {
        @Override
        public String toString() {
            return "处理中";
        }
    },
    /**
     * 处理成功
     */
    SUCCESS() {
        @Override
        public String toString() {
            return "处理成功";
        }
    },
    /**
     * 处理失败
     */
    FAILED() {
        @Override
        public String toString() {
            return "处理失败";
        }
    };

    @Override
    public abstract String toString();
}
