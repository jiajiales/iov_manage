package com.cennavi.audi_data_collect.service;


import com.cennavi.audi_data_collect.bean.ParamsBean;
import com.cennavi.audi_data_collect.dao.HomeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by cennavi on 2019/4/29.
 */
@Service
public class HomeService {
    @Autowired
    private HomeDao homeDao;

    public byte[] getVRUEvent(int x,int y,int z,String sDate,String eDate,String dates,String sTime,String eTime,int orderType,String road) throws Exception{
        return homeDao.getVRUEvent(x,y,z,sDate,eDate,dates,sTime,eTime,orderType,road );
    }

    public byte[] getSegmentId(int x,int y,int z) throws Exception{
        return homeDao.getSegmentId(x,y,z);
    }

    public List<Map<String,Object>> getPointInfo(ParamsBean paramsBean){
        return homeDao.getPointInfo(paramsBean);
    }

    public List<Map<String,Object>> getRoad(){
        return homeDao.getRoad();
    }

}
