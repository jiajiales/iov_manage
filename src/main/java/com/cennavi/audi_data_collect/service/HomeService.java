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

    public byte[] getVRUEvent(int x,int y,int z,String city,String eventsList,String dataList,String dataListFormat,String roadSecList,
                              String timeFrame,String isContinuous) throws Exception{
        return homeDao.getVRUEventNew(x,y,z,city,eventsList,dataList,dataListFormat,roadSecList,timeFrame,isContinuous);
    }

    public byte[] getSegmentId(int x,int y,int z) throws Exception{
        return homeDao.getSegmentId(x,y,z);
    }

    public List<Map<String,Object>> getPointInfo(ParamsBean paramsBean){
        return homeDao.getPointInfo(paramsBean);
    }

    public Map<String,Object> getMultiPointInfo(ParamsBean paramsBean) throws Exception{
        return homeDao.getMultiPointInfo(paramsBean);
    }

    public List<Map<String,Object>> getRoad(){
        return homeDao.getRoad();
    }

    public Map<String,Object> getTimesHeat(ParamsBean paramsBean){
        return homeDao.getTimesHeat(paramsBean);
    }

}
