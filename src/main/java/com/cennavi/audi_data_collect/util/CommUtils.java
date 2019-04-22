package com.cennavi.audi_data_collect.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 公共变量存放工具
 * @author Admin
 *
 */
public class CommUtils {
	public static final int SUCCODE = 200;//请求成功
	public static final int ERRCODE = 500;//服务器内部错误，无法完成请求
	public static final int LOGCODE = 401;//请求要求用户的身份认证
	/**
	 * 两个时间相差距离多少天多少小时多少分多少秒
	 * @param str1 时间参数 1 格式：1990-01-01 12:00:00
	 * @param str2 时间参数 2 格式：2009-01-01 12:00:00
	 * @return long[] 返回值为：{天, 时, 分, 秒}
	 */
	public static long[] getDistanceTimes(String str1, String str2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {
			String strOne=str1.substring(0,4)+"-"+str1.substring(4,6)+"-"+str1.substring(6,8)+" "+str1.substring(8,10)+":"+str1.substring(10,12)+":"+str1.substring(12,14);
			String strTwo=str2.substring(0,4)+"-"+str2.substring(4,6)+"-"+str2.substring(6,8)+" "+str2.substring(8,10)+":"+str2.substring(10,12)+":"+str2.substring(12,14);
			one = df.parse(strOne);
			two = df.parse(strTwo);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff ;
			if(time1<time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = (diff / (60 * 60 * 1000) - day * 24);
			min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long[] times = {day, hour, min, sec};
		return times;
	}
}
