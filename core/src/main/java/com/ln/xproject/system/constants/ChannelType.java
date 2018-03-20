package com.ln.xproject.system.constants;

/**
 * Created by ning on 2/13/17.
 */
public enum ChannelType {
    YI_MEI {
        @Override
        public String toString() {
            return "医美";
        }

        @Override
        public String toProxyPartner() {
            return "YI_MEI";
        }

        @Override
        public String getSerialNo() {
            return "01";
        }

    },
    PAY_DAY_LOAN {
        @Override
        public String toString() {
            return "快速借款";
        }

        @Override
        public String toProxyPartner() {
            return "PAY_DAY_LOAN";
        }

        @Override
        public String getSerialNo() {
            return "02";
        }
    },
    RRD {
        @Override
        public String toString() {
            return "人人贷";
        }

        @Override
        public String toProxyPartner() {
            return "RRD";
        }

        @Override
        public String getSerialNo() {
            return "03";
        }
    },
    HAOHUAN {
        @Override
        public String toString() {
            return "好还";
        }

        @Override
        public String toProxyPartner() {
            return "HAOHUAN";
        }

        @Override
        public String getSerialNo() {
            return "04";
        }
    };

    public abstract String toString();

    public abstract String toProxyPartner();

    public abstract String getSerialNo();
}
