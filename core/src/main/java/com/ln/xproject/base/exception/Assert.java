package com.ln.xproject.base.exception;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.model.BaseModel;
import com.ln.xproject.util.BigDecimalUtils;
import com.ln.xproject.util.StringUtils;

public class Assert {

    public static void notNull(Object obj, String paras) {
        if (obj == null) {
            throw ServiceException.exception(CodeConstants.C_10101001, paras);
        }
    }

    public static void notBlank(String str, String paras) {
        if (StringUtils.isBlank(str)) {
            throw ServiceException.exception(CodeConstants.C_10101001, paras);
        }
    }

    public static void notBlank(Enum<?> str, String paras) {
        if (str == null) {
            throw ServiceException.exception(CodeConstants.C_10101001, paras);
        }
    }

    public static <T extends Collection<?>> void notEmpty(T collection, String paras) {
        if (CollectionUtils.isEmpty(collection)) {
            throw ServiceException.exception(CodeConstants.C_10101001, paras);
        }
    }

    public static void validStringLength(String str, int max, int min, boolean canEmpty, String paras) {
        if (!canEmpty && StringUtils.isBlank(str)) {
            throw ServiceException.exception(CodeConstants.C_10101001, paras);
        } else if (StringUtils.isNotBlank(str)) {
            int length = str.length();
            if (max < length || min > length) {
                throw ServiceException.exception(CodeConstants.C_10101012, paras, min, max);
            }
        }
    }

    public static void isTrue(boolean flag, String paras) {
        if (!flag) {
            throw ServiceException.exception(CodeConstants.C_10101005, paras);
        }
    }

    public static void notExist(BaseModel model, String paras) {
        if (model == null) {
            throw ServiceException.exception(CodeConstants.C_10101005, paras);
        }
    }

    public static void exist(BaseModel model, String paras) {
        if (model != null) {
            throw ServiceException.exception(CodeConstants.C_10101008, paras);
        }
    }

    /**
     * 验证精度
     *
     * @param decimal
     * @param scale
     */
    public static void wrongScale(BigDecimal decimal, Integer scale) {
        notNull(decimal, "验证数值");
        notNull(scale, "验证精度");

        if (decimal.stripTrailingZeros().scale() > scale) {
            throw ServiceException.exception(CodeConstants.C_10101015);
        }
    }

    /**
     * 大于等于(ge)零
     * 
     * @param decimal
     * @param paras
     */
    public static void geZero(BigDecimal decimal, String paras) {
        notNull(decimal, paras);
        if (decimal.compareTo(BigDecimal.ZERO) < 0) {
            throw ServiceException.exception(CodeConstants.C_10101003, paras);
        }
    }

    /**
     * 大于gt零
     * 
     * @param decimal
     * @param paras
     */
    public static void gtZero(BigDecimal decimal, String paras) {
        notNull(decimal, paras);
        if (decimal.compareTo(BigDecimal.ZERO) <= 0) {
            throw ServiceException.exception(CodeConstants.C_10101007, paras);
        }
    }

    /**
     * 大于等于(ge)零
     * 
     * @param integer
     * @param paras
     */
    public static void geZero(Integer integer, String paras) {
        notNull(integer, paras);
        if (integer < 0) {
            throw ServiceException.exception(CodeConstants.C_10101003, paras);
        }
    }

    /**
     * 大于(gt)零
     * 
     * @param integer
     * @param paras
     */
    public static void gtZero(Integer integer, String paras) {
        notNull(integer, paras);
        if (integer < 0) {
            throw ServiceException.exception(CodeConstants.C_10101007, paras);
        }
    }

    /**
     * 小于等于(le)指定参数
     * 
     * @param integer
     * @param paras
     */
    public static void leNum(Integer integer, int num, String paras) {
        notNull(integer, paras);
        if (integer > num) {
            throw ServiceException.exception(CodeConstants.C_10101013, paras, String.valueOf(num));
        }
    }

    /**
     * 枚举是否有效
     * 
     * @param enumType
     *            枚举类型
     * @param name
     *            枚举name
     * @param paras
     */
    public static <T extends Enum<T>> T enumNotValid(Class<T> enumType, String name, String paras) {
        notBlank(name, paras);
        try {
            return Enum.valueOf(enumType, name);
        } catch (Exception e) {
            throw ServiceException.exception(CodeConstants.C_10101004, paras);
        }
    }

    public static void isDate(Long date, String paras) {
        notNull(date, paras);
        String dateStr = date + "";
        if (dateStr.length() != 13) {
            throw ServiceException.exception(CodeConstants.C_10101009);
        }
        try {
            new Date(date);
        } catch (Exception e) {
            throw ServiceException.exception(CodeConstants.C_10101009);
        }
    }

    public static void isDate(String date, String pattenr, String paras) {
        notNull(date, paras);
        SimpleDateFormat format = new SimpleDateFormat(pattenr);
        try {
            // 设置lenient为false.
            // 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(date);
        } catch (Exception e) {
            throw ServiceException.exception(CodeConstants.C_10101021, paras);
        }
    }

    public static void inRange(BigDecimal decimal, BigDecimal min, BigDecimal max, String paras) {
        gtZero(decimal, paras);
        if (decimal.compareTo(min) < 0 || decimal.compareTo(max) > 0) {
            throw ServiceException.exception(CodeConstants.C_10101016, paras, BigDecimalUtils.rounding(min).toString(),
                    BigDecimalUtils.rounding(max).toString());
        }
    }

    public static void validJson(String str, boolean canEmpty, String paras) {
        if (!canEmpty && StringUtils.isBlank(str)) {
            throw ServiceException.exception(CodeConstants.C_10101001, paras);
        } else if (StringUtils.isNotBlank(str)) {
            try {
                JSONObject.parse(str);
            } catch (Exception e) {
                throw ServiceException.exception(CodeConstants.C_10101002, paras + "无效的json格式");
            }
        }
    }

}
