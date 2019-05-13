package com.cennavi.audi_data_collect.service;


import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cennavi.audi_data_collect.bean.CVSBean;
import com.cennavi.audi_data_collect.bean.ParamsBean;
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
	public Object queryHistogram(String cityName, String eventType, String startTime, String endTime,String segmentIds, String startTimeFrames, String endTimeFrames, String isContinuous, String dataLists) {
		return eventUserDao.queryHistogram(cityName,eventType,startTime,endTime,segmentIds,startTimeFrames,endTimeFrames,isContinuous,dataLists);
	}
	//数据统计
	public Object dataStatistics(String cityName, String eventTypes, String startTime, String endTime,
			String segmentIds, String startTimeFrames, String endTimeFrames,String sort, String isContinuous, String dataLists) {
		return eventUserDao.dataStatistics(cityName,eventTypes,startTime,endTime,segmentIds,startTimeFrames,endTimeFrames,sort,isContinuous,dataLists);
	}
	
	//折线图
	public Object brokenLine(String cityName, String eventType, String startTime, String endTime, String segmentIds, String startTimeFrames, String endTimeFrames,String isContinuous ,String dataLists) {
		return eventUserDao.brokenLine(cityName,eventType,startTime,endTime,segmentIds,startTimeFrames,endTimeFrames,isContinuous,dataLists);
	}
	//数据导出
	public List<CVSBean> exportCsvs(ParamsBean paramsBean) throws ParseException {
		return eventUserDao.exportCsvs(paramsBean);
	}
	public Object dataStatistic(ParamsBean paramsBean) throws Exception {
		// TODO Auto-generated method stub
		return eventUserDao.dataStatistic(paramsBean);
	}

}
