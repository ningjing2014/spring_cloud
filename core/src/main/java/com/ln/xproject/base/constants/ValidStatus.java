package com.ln.xproject.base.constants;

/**
 * 有效状态 Created by zhoujiuping on 2016/12/7.
 */
public enum ValidStatus {

    ENABLE {
        @Override
        public String toString() {
            return "有效";
        }
    },
    DISABLE {
        @Override
        public String toString() {
            return "无效";
        }
    };

    @Override
    public abstract String toString();

}
