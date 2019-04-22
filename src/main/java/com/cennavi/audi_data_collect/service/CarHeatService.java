package com.cennavi.audi_data_collect.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cennavi.audi_data_collect.dao.CarHeatDao;

/**
 * Created by cennavi on 2019/3/12.
 */
@Service
public class CarHeatService {
    @Autowired
    private CarHeatDao carHeatDao;

    public long getCurrentNum(String time){
        long num = carHeatDao.getCurrentNum(time);
        return num;
    }

    public List<Map<String,Object>> getTop5(String time){
        List<Map<String,Object>> list = carHeatDao.getTop5(time);
        return list;
    }

    public byte[] getCarHeatMap(int z ,int x ,int y ,String time)throws Exception{
        return carHeatDao.getCarHeatMap(z,x,y,time);
    }


}
