package com.cennavi.audi_data_collect.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cennavi.audi_data_collect.dao.EventHeatDao;

/**
 * Created by cennavi on 2019/4/25.
 */
@Service
public class EventHeatService {
    @Autowired
    private EventHeatDao carHeatDao;

    public List<Map<String, Object>> getGaoSuLines(){
    	return carHeatDao.getGaoSuLines();
    }
    
    public void getDealEventRelationship(){
    	carHeatDao.getDealEventRelationship();
    }
    
    public Map<String, Object> eventAggregateFigure(Map<String, Object> paramMap) throws Exception{
    	return carHeatDao.eventAggregateFigure(paramMap);
    }
    
    public void insertSegment(Map<String, Object> map) {
    	carHeatDao.insertSegment(map);
    }

}
