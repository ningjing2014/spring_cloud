package com.ln.xproject.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具类
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public static  final String DATE_TIME="yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN_YYMMDD = "yyMMdd";

    public static final int ONE_SECOND = 1000;
    public static final int ONE_MINUTE = ONE_SECOND * 60;
    public static final int ONE_HOUR = ONE_MINUTE * 60;
    public static final int ONE_DAY = ONE_HOUR * 24;
    public static final int ONE_DAY_SECONDS = 24 * 60 * 60;

    /**
     * 设置为某日期的开始00:00:00
     * 
     * @param date
     * @return
     */
    public static Date setDateBegin(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 设置为某日期的结束23:59:59
     * 
     * @param date
     * @return
     */
    public static Date setDateEnd(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    /**
     * 得到本月对应的第一天
     * 
     * @return String
     */
    public static String getMonthFristDay() {
        Calendar cld = Calendar.getInstance();
        cld.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        String firstDay = dft.format(cld.getTime());
        return firstDay;
    }

    /**
     * 得到本月对应的最后一天
     * 
     * @return String
     */
    public static String getMonthEndDay() {
        Calendar cld = Calendar.getInstance();
        cld.add(Calendar.MONTH, 1);
        cld.set(Calendar.DAY_OF_MONTH, 0);
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        String endDay = dft.format(cld.getTime());
        return endDay;
    }

    /**
     * 得到指定月的第一天
     * 
     * @return String
     */
    public static String getFristDayInMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        String firstDay = dft.format(calendar.getTime());
        return firstDay;
    }

    /**
     * 得到指定月的最后一天
     * 
     * @return String
     */
    public static String getEndDayInMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month + 1, 0);
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        String endDay = dft.format(calendar.getTime());
        return endDay;
    }

    /**
     * 获得指定格式的日期字符串
     * 
     * @return
     */
    public static String getCurrentDate(String format) {
        String dateString = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            Date currentTime_1 = new Date();
            dateString = formatter.format(currentTime_1);
        } catch (Exception e) {
        }
        return dateString;
    }

    /**
     * 转换时间字符串为日期和时间
     * 
     * @param s
     * @return
     */
    public static Date getFormatTime(String s) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            return simpleDateFormat.parse(s);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 获取后几天的日期
     * 
     * @param num
     * @return
     */
    public static Date getNextDayDate(Date cDate, int num) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Calendar date = Calendar.getInstance();
        try {
            date.setTime(cDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        date.set(Calendar.DATE, date.get(Calendar.DATE) + num);
        String nextDate = dft.format(date.getTime()) + " 00:00:00";
        Date ndate = null;
        try {
            ndate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(nextDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ndate;
    }

    /**
     * 格式化日期
     * 
     * @param date
     * @param format
     * @return
     */
    public static String getFormatDate(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 获取当前月天数
     *
     * @return
     */
    public static int getCurMonthDays() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 转换日期，转换失败时返回null
     * 
     * @param str
     * @param parsePatterns
     * @return
     */
    public static Date parse(String str, String parsePatterns) {
        try {
            return parseDate(str, parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String format(Date date, String parsePatterns) {
        return new SimpleDateFormat(parsePatterns).format(date);
    }

    /**
     * 日期(yyyy-MM-dd)
     * 
     * @param date
     * @return
     */
    public static Date parseSimpleDate(Date date) {
        String pattern = "yyyy-MM-dd";
        try {
            return parseDate(DateFormatUtils.format(date, pattern), pattern);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 相差天数
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static Integer differenceDays(Date date1, Date date2) {
        date1 = parseSimpleDate(date1);
        date2 = parseSimpleDate(date2);
        return (int) ((date1.getTime() - date2.getTime()) / ONE_DAY);
    }

    public static Date getFormatMiddleDate(Date date) {
        String dateStr = DateUtils.format(date, "yyyy-MM-dd") + " 12:00:00";
        return parse(dateStr, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 判断时间是否在时间段内
     * 
     * @param date
     *            当前时间 yyyy-MM-dd HH:mm:ss
     * @param strDateBegin
     *            开始时间 00:00:00
     * @return
     */
    public static boolean isInTime(Date date, String strDateBegin) {
        // String strDateBegin = "20:00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String strDate = sdf.format(date); // 2017-04-14 20:00:00
        // 截取当前时间时分秒 转成整型
        int tempDate = Integer
                .parseInt(strDate.substring(11, 13) + strDate.substring(14, 16) + strDate.substring(17, 19));
        // 截取开始时间时分秒 转成整型
        int tempDateBegin = Integer
                .parseInt(strDateBegin.substring(0, 2) + strDateBegin.substring(3, 5) + strDateBegin.substring(6, 8));
        if ((tempDate >= tempDateBegin)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取延迟秒数时间
     *
     * @param delaySeconds
     * @return
     */
    public static Date getDelayDate(int delaySeconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.SECOND, delaySeconds);
        return cal.getTime();
    }

    public static Date getDelayDate(Date date, int delaySeconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, delaySeconds);
        return cal.getTime();
    }

    public static void main(String[] args) {

        System.out.println(DateUtils.getDelayDate(100));
    }

}