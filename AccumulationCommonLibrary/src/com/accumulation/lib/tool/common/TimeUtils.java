package com.accumulation.lib.tool.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class TimeUtils {

	public final static String FORMAT_YEAR = "yyyy";
	public final static String FORMAT_MONTH_DAY = "MM月dd日";
	public final static String FORMAT_MONTH_DAY_1 = "MM-dd";

	public final static String FORMAT_DATE = "yyyy-MM-dd";
	public final static String FORMAT_TIME = "HH:mm";
	public final static String FORMAT_TIME_1 = "hh:mm";

	public final static String FORMAT_MONTH_DAY_TIME = "MM月dd日  hh:mm";

	public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
	public final static String FORMAT_DATE1_TIME = "yyyy/MM/dd HH:mm";
	public final static String FORMAT_DATE_TIME_SECOND = "yyyy/MM/dd HH:mm:ss";
	public final static String FORMAT_DATE_TIME_SECOND_1 = "yyyy-MM-dd HH:mm:ss";

	public final static String FORMAT_DATE_TIME_FOR_SAVE = "yyyy-MM-dd_ HH:mm:ss";

	private static SimpleDateFormat sdf = new SimpleDateFormat();
	public static final int YEAR = 365 * 24 * 60 * 60;// 年
	public static final int MONTH = 30 * 24 * 60 * 60;// 月
	public static final int DAY = 24 * 60 * 60;// 天
	public static final int HOUR = 60 * 60;// 小时
	public static final int MINUTE = 60;// 分钟
	public static final String[] WEEK = {"星期天","星期一","星期二","星期三","星期四","星期五","星期六",};// 分钟
	public static final int DAY_MS = 24 * 60 * 60*1000;// 天

	/**
	 * 根据时间戳获取描述性时间，如3分钟前，1天前
	 * 
	 * @param timestamp
	 *            时间戳 单位为毫秒
	 * @return 时间字符串
	 */
	public static String getDescriptionTimeFromTimestamp(long timestamp) {
		long currentTime = System.currentTimeMillis();
		long timeGap = (currentTime - timestamp) / 1000;// 与现在时间相差秒数
		System.out.println("timeGap: " + timeGap);
		String timeStr = null;
		if (timeGap > YEAR) {
			timeStr = timeGap / YEAR + "年前";
		} else if (timeGap > MONTH) {
			timeStr = timeGap / MONTH + "个月前";
		} else if (timeGap > DAY) {// 1天以上
			timeStr = timeGap / DAY + "天前";
		} else if (timeGap > HOUR) {// 1小时-24小时
			timeStr = timeGap / HOUR + "小时前";
		} else if (timeGap > MINUTE) {// 1分钟-59分钟
			timeStr = timeGap / MINUTE + "分钟前";
		} else {// 1秒钟-59秒钟
			timeStr = "刚刚";
		}
		return timeStr;
	}

	/**
	 * 获取当前日期的指定格式的字符串
	 * 
	 * @param format
	 *            指定的日期时间格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
	 * @return
	 */
	public static String getCurrentTime(String format) {
		if (format == null || format.trim().equals("")) {
			sdf.applyPattern(FORMAT_DATE_TIME);
		} else {
			sdf.applyPattern(format);
		}
		return sdf.format(new Date());
	}

	// date类型转换为String类型
	// formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
	// data Date类型的时间
	public static String dateToString(Date data, String formatType) {
		return new SimpleDateFormat(formatType).format(data);
	}

	// long类型转换为String类型
	// currentTime要转换的long类型的时间
	// formatType要转换的string类型的时间格式
	public static String longToString(long currentTime, String formatType) {
		String strTime = "";
		Date date = longToDate(currentTime, formatType);// long类型转成Date类型
		strTime = dateToString(date, formatType); // date类型转成String
		return strTime;
	}

	// string类型转换为date类型
	// strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
	// HH时mm分ss秒，
	// strTime的时间格式必须要与formatType的时间格式相同
	public static Date stringToDate(String strTime, String formatType) {
		SimpleDateFormat formatter = new SimpleDateFormat(formatType);
		Date date = null;
		try {
			date = formatter.parse(strTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	// long转换为Date类型
	// currentTime要转换的long类型的时间
	// formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
	public static Date longToDate(long currentTime, String formatType) {
		Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
		String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
		Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
		return date;
	}

	// string类型转换为long类型
	// strTime要转换的String类型的时间
	// formatType时间格式
	// strTime的时间格式和formatType的时间格式必须相同
	public static long stringToLong(String strTime, String formatType) {
		Date date = stringToDate(strTime, formatType); // String类型转成date类型
		if (date == null) {
			return 0;
		} else {
			long currentTime = dateToLong(date); // date类型转成long类型
			return currentTime;
		}
	}

	// date类型转换为long类型
	// date要转换的date类型的时间
	public static long dateToLong(Date date) {
		return date.getTime();
	}

	public static String getTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
		return format.format(new Date(time));
	}

	public static String getHourAndMin(long time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(new Date(time));
	}

	/**
	 * 获取聊天时间：因为sdk的时间默认到秒故应该乘1000
	 * 
	 * @Title: getChatTime
	 * @Description: TODO
	 * @param @param timesamp
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String getChatTime(long timesamp) {
		long clearTime = timesamp;
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		Date today = new Date(System.currentTimeMillis());
		Date otherDay = new Date(clearTime);
		int temp = Integer.parseInt(sdf.format(today))
				- Integer.parseInt(sdf.format(otherDay));

		switch (temp) {
		case 0:
			result = "今天 " + getHourAndMin(clearTime);
			break;
		case 1:
			result = "昨天 " + getHourAndMin(clearTime);
			break;
		case 2:
			result = "前天 " + getHourAndMin(clearTime);
			break;

		default:
			result = getTime(clearTime);
			break;
		}

		return result;
	}

	public static Date changeToDate(String s) {
		if (s == null) {
			return new Date();
		}
		long time = Long.parseLong(s.substring(s.indexOf("(") + 1,
				s.indexOf(")")));
		return new Date(time);
	}
	public static String formatShowTIme(String time){
		long t=stringToDate(time,FORMAT_DATE_TIME).getTime();
		return formatShowTIme(t);
	}
	public static String formatShowTIme(long time){
		long now=System.currentTimeMillis();
		int offset=(int) (((now-now%DAY_MS)-(time-time%DAY_MS))/DAY_MS);
		switch(offset){
		case 0:
			Calendar c=Calendar.getInstance();
			c.setTime(new Date(time));
			boolean am=c.get(Calendar.AM_PM)==Calendar.AM;
			return (am?"上午":"下午")+longToString(time, FORMAT_TIME_1);
		case 1:
			return "昨天";
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			c=Calendar.getInstance();
			c.setTime(new Date(time));
			return WEEK[c.get(Calendar.WEDNESDAY)];
		default:
			return longToString(time, FORMAT_DATE);
		}
	}
	public static String formatChatTIme(String time){
		long t=stringToDate(time,FORMAT_DATE_TIME).getTime();
		return formatChatTIme(t);
	}
	
	public static boolean isSameMinute(String time1,String time2){
		long t1=stringToDate(time1,FORMAT_DATE_TIME).getTime();
		long t2=stringToDate(time2,FORMAT_DATE_TIME).getTime();
		final  int MINUTE_MS=60*1000;
		return t1/MINUTE_MS==t2/MINUTE_MS;
	}
	public static String formatChatTIme(long time){
		Calendar c=Calendar.getInstance();
		c.setTime(new Date(time));
		boolean am=c.get(Calendar.AM_PM)==Calendar.AM;
		String part_one= (am?"上午":"下午")+longToString(time, FORMAT_TIME_1);
		String part_two="";
		long now=System.currentTimeMillis();
		int offset=(int) (((now-now%DAY_MS)-(time-time%DAY_MS))/DAY_MS);
		switch(offset){
		case 0:
			part_two="";
			break;
		case 1:
			part_two="昨天";
			break;
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			c=Calendar.getInstance();
			c.setTime(new Date(time));
			part_two=WEEK[c.get(Calendar.WEDNESDAY)];
			break;
		default:
			part_two= longToString(time, FORMAT_DATE);
			break;
		}
		return part_two+" "+part_one;
	}
}