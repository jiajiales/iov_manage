package com.cennavi.vehicle_networking_data.beans;

public class OverSpeedBean {
	public long time;
	public String point;
	public int scale;	//超速比例
	public int speed;
	
	public OverSpeedBean(long time, String point, int scale, int speed) {
		this.time = time;
		this.point = point;
		this.scale = scale;
		this.speed = speed;
	}
}
