package com.ln.xproject.application.constants;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yushijun
 * @date 2018/1/23
 */
public enum VerifyBusinessType {

    DISTRIBUTE() {
        @Override
        public String toString() {
            return "分配任务";
        }

        @Override
        public EnumSet<ApplicationAuditStatus> auditStatusSet() {
            return EnumSet.of(ApplicationAuditStatus.TO_DISTRIBUTE);
        }
    },
    AUDIT() {
        @Override
        public String toString() {
            return "代办任务";
        }

        @Override
        public EnumSet<ApplicationAuditStatus> auditStatusSet() {
            return EnumSet.of(ApplicationAuditStatus.TO_AUDIT, ApplicationAuditStatus.SPECIAL);
        }
    },
    LIST() {
        @Override
        public String toString() {
            return "进件查询";
        }

        @Override
        public EnumSet<ApplicationAuditStatus> auditStatusSet() {
            return EnumSet.allOf(ApplicationAuditStatus.class);
        }
    },
    TERMINAL() {
        @Override
        public String toString() {
            return "办结任务";
        }

        @Override
        public EnumSet<ApplicationAuditStatus> auditStatusSet() {
            return EnumSet.of(ApplicationAuditStatus.APPROVE, ApplicationAuditStatus.REJECT);
        }
    };

    @Override
    public abstract String toString();

    public abstract EnumSet<ApplicationAuditStatus> auditStatusSet();

    public boolean isOwnedStatus(ApplicationAuditStatus auditStatus) {
        return this.auditStatusSet().contains(auditStatus);
    }

    public List<ApplicationAuditStatus> ownedAuditStatus() {
        return this.auditStatusSet().stream().collect(Collectors.toList());
    }

}
