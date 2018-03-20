package com.ln.xproject.base.exception;

import com.ln.xproject.base.code.Code;

public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = -4545653946365722562L;

    private String code;

    private ServiceException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ServiceException exception(Code code) {
        return new ServiceException(code.getCode(), code.getMessage());
    }

    public static ServiceException exception(Code code, Object... paras) {
        return new ServiceException(code.getCode(), String.format(code.getMessage(), paras));
    }

}
