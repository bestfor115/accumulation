package com.accumulation.lib.utility.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhangyl on 2016/7/28.
 */
public class TimeUtils {
    public static final int DAY = 24 * 60 * 60;// 天
    public static final int HOUR = 60 * 60;// 小时
    public static final int MINUTE = 60;// 分钟
    public static final int DAY_MS = 24 * 60 * 60*1000;// 天
    public static final String[] WEEK = {"星期天","星期一","星期二","星期三","星期四","星期五","星期六",};

    public final static String FORMAT_YEAR = "yyyy";
    public final static String FORMAT_MONTH_DAY_CHINESE = "MM月dd日";
    public final static String FORMAT_MONTH_DAY_LINE = "MM-dd";
    public final static String FORMAT_MONTH_DAY_TIME = "MM月dd日 hh:mm";

    public final static String FORMAT_TIME_24 = "HH:mm";
    public final static String FORMAT_TIME_12 = "hh:mm";

    public final static String FORMAT_DATE_LINE = "yyyy-MM-dd";
    public final static String FORMAT_DATE_CHINESE = "yyyy年MM月dd日";

    public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
    public final static String FORMAT_DATE1_TIME_OBILQUE_LINE = "yyyy/MM/dd HH:mm";
    public final static String FORMAT_DATE_TIME_SECOND_OBILQUE_LINE = "yyyy/MM/dd HH:mm:ss";
    public final static String FORMAT_DATE_TIME_SECOND_LINE = "yyyy-MM-dd HH:mm:ss";

    /***
     * 按制定格式格式化时间
     *
     * @param date 传入时间
     * @param formatType 格式化字符串
     * @return 格式化后的时间字符串
     * */
    public static String dateToString(Date date, String formatType) {
        return new SimpleDateFormat(formatType).format(date);
    }

    /***
     * 按制定格式格式化时间
     *
     * @param timeInMillis 传入时间的毫秒值
     * @param formatType 格式化字符串
     * @return 格式化后的时间字符串
     * */
    public static String longToString(long timeInMillis, String formatType) {
        return new SimpleDateFormat(formatType).format(new Date(timeInMillis));
    }

    /***
     * 按制定格式格式化当前时间
     *
     * @param formatType 格式化字符串
     * @return 格式化后的时间字符串
     * */
    public static String nowToString(String formatType) {
        return new SimpleDateFormat(formatType).format(new Date());
    }

    /***
     * 将字符串格式化为时间对象
     *
     * @param dateStr 传入时间字符串
     * @param formatType 格式化字符串
     * @return 格式化后的时间对象
     * */
    public static Date stringToDate(String dateStr, String formatType) {
        try {
            return new SimpleDateFormat(formatType).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }
}
