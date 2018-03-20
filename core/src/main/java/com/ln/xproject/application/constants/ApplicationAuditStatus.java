package com.ln.xproject.application.constants;

import java.util.EnumSet;

import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.exception.ServiceException;

/**
 * 进件审核状态
 * 
 * @author taixin
 */
public enum ApplicationAuditStatus {
    TO_DISTRIBUTE() {
        @Override
        public String toString() {
            return "待分配";
        }
    },
    TO_AUDIT() {
        @Override
        public String toString() {
            return "待审核";
        }
    },
    SPECIAL() {
        @Override
        public String toString() {
            return "特殊状态";
        }
    },
    APPROVE() {
        @Override
        public String toString() {
            return "审核通过";
        }
    },
    REJECT() {
        @Override
        public String toString() {
            return "审核驳回";
        }
    };

    @Override
    public abstract String toString();

    public static final EnumSet<ApplicationAuditStatus> MANUAL_AUDIT_RESULT = EnumSet.of(SPECIAL, APPROVE, REJECT);

    public static void checkAuditStatus(ApplicationAuditStatus nowStatus, ApplicationAuditStatus targetStatus) {

        boolean canSet = false;

        switch (targetStatus) {
            case TO_DISTRIBUTE:
                break;
            case TO_AUDIT:
                if (nowStatus == ApplicationAuditStatus.TO_DISTRIBUTE) {
                    canSet = true;
                }
                break;
            case SPECIAL:
                if (nowStatus == ApplicationAuditStatus.TO_AUDIT) {
                    canSet = true;
                }
            case APPROVE:
                if (nowStatus == ApplicationAuditStatus.TO_AUDIT || nowStatus == ApplicationAuditStatus.SPECIAL) {
                    canSet = true;
                }
                break;
            case REJECT:
                if (nowStatus == ApplicationAuditStatus.TO_AUDIT || nowStatus == ApplicationAuditStatus.SPECIAL) {
                    canSet = true;
                }
                break;
            default:
                throw ServiceException.exception(CodeConstants.C_10101020);
        }
        if (!canSet) {
            throw ServiceException.exception(CodeConstants.C_10101010, "进件", nowStatus.toString(),
                    targetStatus.toString());
        }
    }
}
