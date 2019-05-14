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
	
	//数据导出
	public List<CVSBean> exportCsvs(ParamsBean paramsBean) throws ParseException {
		return eventUserDao.exportCsvs(paramsBean);
	}
	
	//数据统计
	public Object dataStatistic(ParamsBean paramsBean) throws Exception {
		// TODO Auto-generated method stub
		return eventUserDao.dataStatistic(paramsBean);
	}
	
	//折线图
	public Object brokenLines(ParamsBean paramsBean) {
		// TODO Auto-generated method stub
		return  eventUserDao.brokenLines(paramsBean);
	}
	//柱状图
	public Object queryHistograms(ParamsBean paramsBean) {
		// TODO Auto-generated method stub
		return eventUserDao.queryHistograms(paramsBean);
	}

}
