package com.cennavi.vehicle_networking_data.utils;

public class AnalysisOptions {

	//时间间隔
	public static int define_span_time = 60 * 15;			//15分钟
	
	//限速值
	public static int speed_limit = 80;				//km/h
	
	//报警类型
	//public static int alam_normal_driving = 0;			//正常
	public static int alam_normal_inactive = 0;			//非活跃车
	public static int alam_normal_active = 1;			//活跃车
	
	public static int alam_over_speed = 4;				//超速
	public static int alam_fatigue_driving = 5;			//疲劳驾驶	
	public static int alam_parking_not_extinguished = 6;//停车未熄火 
	public static int alam_rapid_acceleration = 7;		//急加速
	public static int alam_sharp_slowdown = 8;			//急减速
	public static int alam_hard_braking = 9;			//急刹车
	
	public static int alam_drive_normal = 20;			//不报警
	public static int alam_drive_in = 21;				//驶入报警
	public static int alam_drive_out = 22;				//驶出报警
	public static int alam_drive_in_out = 23;			//驶入驶出报警
	
	//疲劳驾驶时间阈值(4小时)
	public static long fatigue_driving_time = 4 * 60 * 60 * 1000;
	
	
	
	
}
