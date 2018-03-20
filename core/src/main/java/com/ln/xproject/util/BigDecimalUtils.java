package com.ln.xproject.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.exception.ServiceException;

public class BigDecimalUtils {

    /** 默认精度，2位 */
    public static final int DEFAULT_PERCISION = 2;
    /** 利率精度，15位 */
    public static final int INTEREST_PERCISION = 15;
    /** 计算过程中精度，6位 */
    public static final int MIDDLE_PROCESS_PERCISION = 6;
    /** 默认四舍五入规则 */
    public static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;

    public static BigDecimal rounding(BigDecimal value) {
        Assert.notNull(value, "值");
        return value.setScale(DEFAULT_PERCISION, DEFAULT_ROUNDING);
    }

    /**
     * 校验精度
     * 
     * @param value
     * @param scale
     * @return boolean
     */
    public static boolean validateScale(BigDecimal value, int scale) {
        Assert.notNull(value, "值");
        return value.stripTrailingZeros().scale() <= scale;
    }

    public static boolean isEquals(BigDecimal value1, BigDecimal value2) {
        Assert.notNull(value1, "值");
        Assert.notNull(value2, "值");
        if (value1.compareTo(value2) == 0) {
            return true;
        }
        return false;
    }

    /**
     * 连乘
     * 
     * @param decimals
     * @return
     */
    public static BigDecimal multiply(BigDecimal... decimals) {
        return multiply(DEFAULT_PERCISION, decimals);
    }

    /**
     * 连乘
     * 
     * @param scale
     * @param decimals
     * @return
     */
    public static BigDecimal multiply(int scale, BigDecimal... decimals) {
        BigDecimal result = BigDecimal.ONE;
        for (BigDecimal decimal : decimals) {
            result = result.multiply(decimal);
        }
        return result.setScale(scale, DEFAULT_ROUNDING);
    }

    /**
     * 计算过程除,保留6位小数
     * 
     * @param decimal
     *            总数
     * @param divisor
     *            除数
     * @return
     */
    public static BigDecimal divide(BigDecimal decimal, BigDecimal divisor) {
        return decimal.divide(divisor, BigDecimalUtils.MIDDLE_PROCESS_PERCISION, BigDecimalUtils.DEFAULT_ROUNDING);
    }

    /**
     * 如果expr1不是NULL，ifNull()返回expr1，否则它返回expr2
     * 
     * @param expr1
     * @param expr2
     * @return
     */
    public static BigDecimal ifNull(BigDecimal expr1, BigDecimal expr2) {
        if (expr1 == null) {
            return expr2;
        }
        return expr1;
    }

    /**
     * 如果expr1不是NULL，ifNull()返回expr1，否则它返回0
     *
     * @param expr1
     * @return
     */
    public static BigDecimal ifNullDefaultZero(BigDecimal expr1) {
        return ifNull(expr1, BigDecimal.ZERO);
    }

    /**
     * 保留两位小数，返回String类型
     * 
     * @param value
     * @return
     */
    public static String toString(BigDecimal value) {
        if (null == value) {
            return "";
        }
        return rounding(value).toPlainString();
    }

    /**
     * 保留两位小数，去除末尾的0，返回String类型
     * 
     * @param value
     * @return
     */
    public static String toStringNoTrailZeros(BigDecimal value) {
        if (null == value) {
            return "";
        }
        return rounding(value.stripTrailingZeros()).toPlainString();
    }

    public static BigDecimal add(BigDecimal expr1, BigDecimal expr2) {
        return ifNullDefaultZero(expr1).add(ifNullDefaultZero(expr2));
    }

    /**
     * 字符串转BigDecimal，返回BigDecimal类型
     *
     * @param decimal
     * @return
     */
    public static BigDecimal parseBigDecimal(String decimal, String paras) {
        try {
            return new BigDecimal(decimal);
        } catch (Exception e) {
            throw ServiceException.exception(CodeConstants.C_10101022, paras);
        }
    }

}
