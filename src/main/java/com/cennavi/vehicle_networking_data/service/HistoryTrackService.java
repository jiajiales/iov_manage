package com.cennavi.vehicle_networking_data.service;

import com.cennavi.vehicle_networking_data.dao.HistoryTrackDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 60195 on 2019/10/15.
 */
@Service
public class HistoryTrackService {

    @Autowired
    private HistoryTrackDao historyTrackDao;

    public Object getHistoryTrack(String key, String sTime, String eTime, int type, int isOwn ){

        return historyTrackDao.getHistoryTrack(key, sTime, eTime,type,isOwn);

    }
}
