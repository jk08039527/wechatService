package com.jerry.wechatservice.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期类
 *
 * @author Tina
 */
public class DateUtils {

    private static final SimpleDateFormat FORMAT_DATE_TIME = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);

    /**
     * @param time
     * @return
     */
    public static synchronized String getTimeByLong(long time) {
        Date date = new Date();
        date.setTime(time);
        return FORMAT_DATE_TIME.format(date);
    }

    /**
     * 格式  yyyy-MM-dd HH:mm:ss
     */
    public static boolean afterCurrent(String timeStr) {
        if (timeStr == null || timeStr.length() < 19) {
            return false;
        }
        try {
            Date time = FORMAT_DATE_TIME.parse(timeStr.substring(0, 19));
            Date current = Calendar.getInstance().getTime();
            return time.getTime() > current.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
