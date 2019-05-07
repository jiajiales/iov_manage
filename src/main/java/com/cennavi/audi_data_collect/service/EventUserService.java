package com.cennavi.audi_data_collect.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cennavi.audi_data_collect.dao.EventUserDao;

@Service
public class EventUserService {
	@Autowired
    private EventUserDao eventUserDao;
	
	 //验证用户信息
	public boolean check(String name, String password) {
		return eventUserDao.check(name,password);
	}
	//类型列表
	public Object queryEventType() {
		return eventUserDao.queryEventType();
	}
	
	//直方图
	public Object queryHistogram(String cityName, String eventType, String startTime, String endTime,String segmentIds, String startTimeFrames, String endTimeFrames) {
		return eventUserDao.queryHistogram(cityName,eventType,startTime,endTime,segmentIds,startTimeFrames,endTimeFrames);
	}
	//数据统计
	public Object dataStatistics(String cityName, String eventTypes, String startTime, String endTime,
			String segmentIds, String startTimeFrames, String endTimeFrames,String sort) {
		
		return eventUserDao.dataStatistics(cityName,eventTypes,startTime,endTime,segmentIds,startTimeFrames,endTimeFrames,sort);
	}
	
	//折线图
	public Object brokenLine(String cityName, String eventType, String startTime, String endTime, String segmentIds, String startTimeFrames, String endTimeFrames) {
		return eventUserDao.brokenLine(cityName,eventType,startTime,endTime,segmentIds,startTimeFrames,endTimeFrames);
	}

}
