package org.kesar.lazy.lazydb.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间转换工具类
 * Created by kesar on 2016/6/22 0022.
 */
public final class DateUtil
{
    public static String date2String(Date time)
    {
        DateFormat format = SimpleDateFormat.getDateTimeInstance();
        return format.format(time);
    }

    public static Date string2Date(String time) throws ParseException
    {
        DateFormat format = SimpleDateFormat.getDateTimeInstance();
        return format.parse(time);
    }
}
