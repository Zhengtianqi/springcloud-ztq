package com.ztq.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 日期时间工具类
 * @author zhengtianqi
 */
public class TimeUtils {
    public static final String FORMAT1 = "yyyyMMddHHmmss";
    public static final String FORMAT2 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT3 = "yyyy-MM-dd";
    public static final String FORMAT4 = "yyyy年MM月dd日";
    public static final String FORMAT5 = "yyyy";
    public static final String FORMAT6 = "yyyyMMddHHmmssSSS";
    public static final String FORMAT7 = "yyyyMMdd";
    public static final String FORMAT8 = "yyyy/MM/dd HH:mm:ss";

    private static final Logger logger = LoggerFactory.getLogger(TimeUtils.class);

    public static Date convertStringToDate(String date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            logger.error("字符串转换Date失败");
        }
        return null;
    }

    public static String convertDateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static LocalDate date2LocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        // atZone()方法返回在指定时区从此Instant生成的ZonedDateTime。
        return instant.atZone(zoneId).toLocalDate();
    }

    /**
     * 获取最近四年,按正序排列
     */
    public static List<String> getLastFourYear() {
        List<String> lastFourYears = new LinkedList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -4);

        for (int i = 0; i < 4; i++) {
            calendar.add(Calendar.YEAR, 1);
            lastFourYears.add(Integer.valueOf(calendar.get(Calendar.YEAR)).toString());
        }

        return lastFourYears;
    }

    public static void main(String[] args) {
        System.out.println(getLastFourYear());
    }
}
