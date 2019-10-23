package com.cennavi.vehicle_networking_data.service;

import com.alibaba.fastjson.JSONObject;
import com.cennavi.vehicle_networking_data.dao.CarTrackDao;
import com.cennavi.vehicle_networking_data.dao.HomeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by 60195 on 2019/9/18.
 */
@Service
public class HomeService {

    @Autowired
    private HomeDao homeDao;

    @Autowired
    private CarTrackDao carTrackDao;

    public byte[] realtimeScatter(int x,int y, int z){
        return homeDao.realtimeScatter(x,y,z);
    }

    public byte[] realtimeScatterByCondition(int x,int y, int z,String business, String group){
        return homeDao.realtimeScatterByCondition(x,y,z,business,group);
    }

    public byte[] heatMap(int z, int x, int y){
        return homeDao.heatMap(z,x,y);
    }

    public JSONObject realtimeTracking(String cph, String time, int group){
        return homeDao.realtimeTracking(cph,time,group);
    }

    public Map<String,Object> getCarMsg(String cph,String date, int type){
        return carTrackDao.getCarMsg(cph,date, type);
    }

    public Map<String,Object> carSimpleInfo(String cph){
        return carTrackDao.carSimpleInfo(cph);
    }


}
