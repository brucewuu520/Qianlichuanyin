package com.brucewuu.android.qlcy.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    private static final ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

	/**
	 * 字符串转换为日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date ParseDate(String date) {
		try {
			return dateFormater.get().parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

        return null;
	}

	/**
	 * 将日期转换为字符串
	 * 
	 * @param date
	 * 时间格式，必须符合yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String ParseDateToString(Date date) {
		return dateFormater.get().format(date);
	}

	/**
	 * 格式化时间格式为yyyy-MM-dd hh:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static Date formatDate(Date date) {
		return ParseDate(ParseDateToString(date));
	}

    /**
     * 获取当前时间（小时）
     */
    public static int getHour() {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        return now.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 是否为白天
     * @return
     */
    public static boolean isSun() {
        int currentHour = getHour();
        if (currentHour > 6 && currentHour < 18) {
            return true;
        }
        return false;
    }

    /**
     * 是早上6-9点
     * @return
     */
    public static boolean isMorning() {
        int currentHour = getHour();
        if (currentHour > 6 && currentHour < 9) {
            return true;
        }
        return false;
    }

    public static String friendlyDate(long time) {
        Date date = new Date(time);
        String timeStr = ParseDateToString(date);
        return timeStr.substring(5, 18);
    }

}
