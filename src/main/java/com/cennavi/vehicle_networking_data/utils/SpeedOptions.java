package com.cennavi.vehicle_networking_data.utils;

import org.apache.commons.collections.CollectionUtils;
//import com.cennavi.beans.OverSpeedBean;

import com.cennavi.vehicle_networking_data.beans.OverSpeedBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpeedOptions {

	public static int alam_normal_driving = 0; // 正常

	public static int alam_rapid_acceleration = 1; // 急加速
	public static int alam_sharp_slowdown = 2; // 急减速
	public static int alam_hard_braking = 3; // 急刹车

	public static int alam_over_speed = 4; // 超速
	public static int alam_fatigue_driving = 5; // 疲劳驾驶

	public static int alam_parking_not_extinguished = 6;// 停车未熄火

	public static String getAlamLevelInfo(int level) {
		String res = "";
		switch (level) {
		case -1:
			res = "离线";
			break;
		case 0:
			res = "在线";
			break;
		case 1:
			res = "急加速";
			break;
		case 2:
			res = "急减速";
			break;
		case 3:
			res = "急刹车";
			break;
		case 4:
			res = "超速";
			break;
		case 5:
			res = "疲劳驾驶";
			break;
		case 6:
			res = "停车未熄火";
			break;
		}
		return res;
	}

	// 超速段比例
	public static int getOverSpeedScale(int speed) {
		float normal = AnalysisOptions.speed_limit;
		float scale = speed / normal;

		// 正常
		if (speed <= normal) {
			return 0;
		}
		// 超速比例1
		if (scale < 1.2) {
			return 1;
		}
		// 超速比例2
		if (scale < 1.5) {
			return 2;
		}
		// 超速比例3 - scale > 1.5
		return 3;
	}

	// 超速数据按段收集
	public static List<List<OverSpeedBean>> splitOverSpeed(List<Integer> listSpeed, List<String> listPoint, List<Long> listTime) {
		if (listSpeed.isEmpty()) {
			return null;
		}
		// 第一个点的速度值
		int startSpeed = listSpeed.remove(0);
		int startScale = getOverSpeedScale(startSpeed);
		// 第一个点的位置
		String startPoint = listPoint.remove(0);
		// 第一个点的时间
		long startTime = listTime.remove(0);

		int flag1 = 0;
		int flag2 = 0;
		int flag3 = 0;
		List<OverSpeedBean> scale1List = new ArrayList<OverSpeedBean>();
		List<OverSpeedBean> scale2List = new ArrayList<OverSpeedBean>();
		List<OverSpeedBean> scale3List = new ArrayList<OverSpeedBean>();
		List<List<OverSpeedBean>> list = new ArrayList<List<OverSpeedBean>>();
		for (int i = 0; i < listSpeed.size(); i++) {
			int speed = listSpeed.get(i);
			int scale = getOverSpeedScale(speed);
			long time = listTime.get(i);
			String point = listPoint.get(i);
			if (scale == 1) {
				if (i == 0) {
					scale1List.add(new OverSpeedBean(startTime, startPoint, startScale, startSpeed));
				}
				scale1List.add(new OverSpeedBean(time, point, scale, speed));
				flag1++;
				flag2 = 0;
				flag3 = 0;
			} else if (scale == 2){
				if (i == 0) {
					scale2List.add(new OverSpeedBean(startTime, startPoint, startScale, startSpeed));
				}
				scale2List.add(new OverSpeedBean(time, point, scale, speed));
				flag2++;
				flag1 = 0;
				flag3 = 0;
			}else if (scale == 3){
				if (i == 0) {
					scale3List.add(new OverSpeedBean(startTime, startPoint, startScale, startSpeed));
				}
				scale3List.add(new OverSpeedBean(time, point, scale, speed));
				flag3++;
				flag1 = 0;
				flag2 = 0;
			}

			if (flag1 == 0) {
				// 复制一个新的List,然后执行任务 dot插入任务
				if (!scale1List.isEmpty()) {
					List<OverSpeedBean> newDotList = new ArrayList<OverSpeedBean>();
					CollectionUtils.addAll(newDotList, new Object[scale1List.size()]);
					Collections.copy(newDotList, scale1List);
					list.add(newDotList);
					scale1List = new ArrayList<OverSpeedBean>();
				}
			}

			if (flag2 == 0) {
				if (!scale2List.isEmpty()) {
					// 复制一个新的List,然后执行任务 dot插入任务
					List<OverSpeedBean> newDotList = new ArrayList<OverSpeedBean>();
					CollectionUtils.addAll(newDotList, new Object[scale2List.size()]);
					Collections.copy(newDotList, scale2List);
					list.add(newDotList);
					scale2List = new ArrayList<OverSpeedBean>();
				}
			}
			
			if (flag3 == 0) {
				if (!scale3List.isEmpty()) {
					// 复制一个新的List,然后执行任务 dot插入任务
					List<OverSpeedBean> newDotList = new ArrayList<OverSpeedBean>();
					CollectionUtils.addAll(newDotList, new Object[scale3List.size()]);
					Collections.copy(newDotList, scale3List);
					list.add(newDotList);
					scale3List = new ArrayList<OverSpeedBean>();
				}
			}
			
			if(speed < AnalysisOptions.speed_limit) {
				if (!scale1List.isEmpty()) {
					List<OverSpeedBean> newDotList = new ArrayList<OverSpeedBean>();
					CollectionUtils.addAll(newDotList, new Object[scale1List.size()]);
					Collections.copy(newDotList, scale1List);
					list.add(newDotList);
					scale1List = new ArrayList<OverSpeedBean>();
				}
				if (!scale2List.isEmpty()) {
					List<OverSpeedBean> newDotList = new ArrayList<OverSpeedBean>();
					CollectionUtils.addAll(newDotList, new Object[scale2List.size()]);
					Collections.copy(newDotList, scale2List);
					list.add(newDotList);
					scale2List = new ArrayList<OverSpeedBean>();
				}
				if (!scale3List.isEmpty()) {
					List<OverSpeedBean> newDotList = new ArrayList<OverSpeedBean>();
					CollectionUtils.addAll(newDotList, new Object[scale3List.size()]);
					Collections.copy(newDotList, scale3List);
					list.add(newDotList);
					scale3List = new ArrayList<OverSpeedBean>();
				}
				
				flag1 = 0;
				flag2 = 0;
				flag3 = 0;
			}

			// 更新
			startSpeed = speed;
		}

		if (!scale1List.isEmpty()) {
			List<OverSpeedBean> newDotList = new ArrayList<OverSpeedBean>();
			CollectionUtils.addAll(newDotList, new Object[scale1List.size()]);
			Collections.copy(newDotList, scale1List);
			list.add(newDotList);
			scale1List = new ArrayList<OverSpeedBean>();
		}
		if (!scale2List.isEmpty()) {
			List<OverSpeedBean> newDotList = new ArrayList<OverSpeedBean>();
			CollectionUtils.addAll(newDotList, new Object[scale2List.size()]);
			Collections.copy(newDotList, scale2List);
			list.add(newDotList);
			scale2List = new ArrayList<OverSpeedBean>();
		}
		if (!scale3List.isEmpty()) {
			List<OverSpeedBean> newDotList = new ArrayList<OverSpeedBean>();
			CollectionUtils.addAll(newDotList, new Object[scale3List.size()]);
			Collections.copy(newDotList, scale3List);
			list.add(newDotList);
			scale3List = new ArrayList<OverSpeedBean>();
		}

		return list;
	}
}
