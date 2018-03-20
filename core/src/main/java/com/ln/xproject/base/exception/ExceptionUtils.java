package com.ln.xproject.base.exception;

import com.ln.xproject.base.code.Code;
import com.ln.xproject.base.code.CodeConstants;

/**
 * Created by ning on 16-12-7.
 */
public class ExceptionUtils {

    public static ServiceException commonError(String errorMsg) {
        return ServiceException.exception(CodeConstants.C_10101002, errorMsg);
    }

    public static ServiceException notImplement(String errorMsg) {
        return ServiceException.exception(CodeConstants.C_10101017, errorMsg);
    }

    public static ServiceException getServiceException(Code code) {
        return ServiceException.exception(code);
    }

}
