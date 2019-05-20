package com.cennavi.audi_data_collect.service;


import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cennavi.audi_data_collect.bean.CVSBean;
import com.cennavi.audi_data_collect.bean.EventPV;
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
	public Object findImages(ParamsBean paramsBean) throws IOException {
		
	        return eventUserDao.findImages(paramsBean);
	}
	public Object editImageDescription(EventPV eventPV) {
		return eventUserDao.editImageDescription(eventPV);
	}
	public Object addVideoComment(EventPV eventPV) {
		// TODO Auto-generated method stub
		return eventUserDao.addVideoComment(eventPV);
	}
	public Object findVideoCommentList(EventPV eventPV) {
		// TODO Auto-generated method stub
		return eventUserDao.findVideoCommentList(eventPV);
	}
	public List<EventPV> exportCsv() {
		// TODO Auto-generated method stub
		return eventUserDao.exportCsv();
	}
	public List<String> findImagesUrl(ParamsBean paramsBean) throws IOException {
		// TODO Auto-generated method stub
		return eventUserDao.findImagesUrl(paramsBean);
	}
	public List<String> findVideosUrl(ParamsBean paramsBean) throws IOException {
		// TODO Auto-generated method stub
		return eventUserDao.findVideosUrl(paramsBean);
	}
	public Object getVideo(ParamsBean paramsBean) throws IOException {
		// TODO Auto-generated method stub
		return eventUserDao.getVideo(paramsBean);
	}

}
