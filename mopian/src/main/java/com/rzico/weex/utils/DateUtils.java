package com.rzico.weex.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Jinlesoft on 2017/10/9.
 */

public class DateUtils {
    /**
     * 指定日期格式 yyyy-MM-dd HH:mm:ss
     */
    public static final String DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";

    /**
     * 指定日期格式 yyyy-MM-dd'T'HH:mm:ssZ
     */
    public static final String DATE_FORMAT_3 = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * 指定日期格式 yyyy-MM-dd
     */
    public static final String DATE_FORMAT_4 = "yyyy-MM-dd";

    /**
     * 指定日期格式 yyyy.M.d
     */
    public static final String DATE_FORMAT_5 = "yyyy.M.d";

    /**
     * 指定日期格式yyyy-MM-dd HH:mm
     */
    public static final String DATE_FORMAT_6 = "yyyy-MM-dd HH:mm";

    /**
     * 指定日期格式HH:mm
     */
    public static final String DATE_FORMAT_7 = "HH:mm";

    /**
     * 指定日期格式MM-dd HH:mm
     */
    public static final String DATE_FORMAT_8 = "MM-dd HH:mm";

    /**
     * 指定日期格式HH:MM:SS
     */
    public static final String DATE_FORMAT_9 = "HH:MM:SS";

    /**
     * 指定日期格式HH:mm
     */
    public static final String DATE_FORMAT_10 = "MM-dd";

    /**
     * 指定日期格式yy-MM-dd HH:mm
     */
    public static final String DATE_FORMAT_11 = "yy-MM-dd HH:mm";

    /**
     * 日期排序类型-升序
     */
    public final static int DATE_ORDER_ASC = 0;

    /**
     * 日期排序类型-降序
     */
    public final static int DATE_ORDER_DESC = 1;

    /**
     * 根据指定格式，获取现在时间
     */
    public static String getNowDateFormat(String format) {
        final Date currentTime = new Date();
        final SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(currentTime);
    }


    /**
     * 比较两个日期的大小，日期格式为yyyy-MM-dd
     * 前者大或晚 则为true
     * @param str2 the second date
     * @return true <br/>false
     */
    public static boolean isDateOneBigger(Date dt1, String str2,String format) {
        boolean isBigger = false;

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dt2 = null;
        try {
            dt2 = sdf.parse(str2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt1.getTime() > dt2.getTime()) {
            isBigger = true;
        } else if (dt1.getTime() < dt2.getTime()) {
            isBigger = false;
        }
        return isBigger;
    }

    /**
     * 比较两个日期的大小，日期格式为yyyy-MM-dd
     *
     * @param str1 the first date
     * @param str2 the second date
     * @return true <br/>false
     */
    public static boolean isDate2Bigger(String str1, String str2) {
        boolean isBigger = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = sdf.parse(str1);
            dt2 = sdf.parse(str2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt1.getTime() > dt2.getTime()) {
            isBigger = false;
        } else if (dt1.getTime() <= dt2.getTime()) {
            isBigger = true;
        }
        return isBigger;
    }
}
