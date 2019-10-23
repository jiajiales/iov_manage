package com.cennavi.vehicle_networking_data.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 60195 on 2019/9/24.
 */
public class TimeUtil {
    // 计算两个时间差，返回为分钟。
    public static long calTime(String time1, String time2) {
        DateFormat df = new SimpleDateFormat("YYYY-MM-DD HH:mm:SS");
        long minutes = 0L;
        try {
            Date d1 = df.parse(time1);
            Date d2 = df.parse(time2);
            minutes = Math.abs((d1.getTime() - d2.getTime()) / (1000 * 60));
        } catch (java.text.ParseException e) {
            System.out.println("时间日期解析出错。");
        }
        return minutes;
    }

    // 计算两个时间戳的时间差，返回为分钟。
    public static long timeDiff(long time1, long time2){
        Long s = (time1 - time2) / (1000* 60);
        return s;
    }

    //时间戳转年月日时分秒
    public static String timeChange(long time1){
        String result1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time1);
        return result1;
    }

    //年月日时分秒转时间戳
    public static long changeTime(String time1) throws java.text.ParseException {
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse(time1);
        //日期转时间戳（毫秒）
        long time=date.getTime();
        return time;
    }

    //获取当前时间5分钟前的时间   格式yyyy-MM-dd HH:mm:ss
    public static String getTimeByMinute(int minute) {

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MINUTE, minute);

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
    }

}
