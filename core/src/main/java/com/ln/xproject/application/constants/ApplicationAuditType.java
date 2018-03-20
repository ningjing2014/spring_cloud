package com.ln.xproject.application.constants;

/**
 * 进件审核类型
 * 
 * @author taixin
 */
public enum ApplicationAuditType {
    SYSTEM() {
        @Override
        public String toString() {
            return "系统审核";
        }
    },
    MANUAL() {
        @Override
        public String toString() {
            return "人工审核";
        }
    };

    public abstract String toString();
}
