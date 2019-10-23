package com.cennavi.vehicle_networking_data.controller;

import com.cennavi.vehicle_networking_data.service.HistoryTrackService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 60195 on 2019/10/15.
 */
@RestController
@RequestMapping(value = "/historyTrack")
public class HistoryTrackController {

    @Autowired
    private HistoryTrackService historyTrackService;

    /**
     * 查询历史轨迹
     * @param key  查询关键信息
     * @param sTime  开始时间
     * @param eTime  结束时间
     * @param type  查询类型 1:按车牌号，2：按设备号
     * @param isOwn  是否对外提供 1：自用，0：对外使用
     * @return
     */
    @ResponseBody
    @RequestMapping("/getHistoryTrack")
    public Map<String,Object> getHistoryTrack(String key, String sTime, String eTime, Integer type,  @RequestParam(value = "isOwn", defaultValue ="0", required = false) int isOwn ){
        Map<String,Object> map = new HashMap<>();
        try {
            Object obj =  historyTrackService.getHistoryTrack(key,sTime,eTime,type,isOwn);
            if(obj == null){
                map.put("data",obj);
                map.put("code",201);
                map.put("msg","查无数据");
                return map;
            }
            map.put("data",obj);
            map.put("code",200);
            map.put("msg","成功");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("data",null);
        map.put("code",500);
        map.put("msg","错误");
        return map;
    }

    @RequestMapping("/test")
    public void test(){
        String aa = "\"{\\\"msgCode\\\":\\\"999\\\",\\\"occurTime\\\":1571722532887,\\\"params\\\":{\\\"lng\\\":114.15149607336865,\\\"locationMode\\\":\\\"1\\\",\\\"gpsTime\\\":1571722528000,\\\"gpsSpeed\\\":\\\"0.0\\\",\\\"battery\\\":\\\"90\\\",\\\"lat\\\":22.686728439862872,\\\"gpsNum\\\":\\\"8\\\",\\\"timestamp\\\":1571722528000},\\\"sourceDeviceId\\\":\\\"868120207982601\\\",\\\"sourceDeviceType\\\":\\\"STAFF\\\",\\\"tag\\\":\\\"STAFF_GPS\\\",\\\"targetDeviceId\\\":\\\"VORTEX__PLAT\\\",\\\"targetDeviceType\\\":\\\"CLOUD\\\",\\\"topic\\\":\\\"STAFF_GPS\\\"}\"";

        System.out.println(aa);

        String bb = aa.replaceAll("\\\\","");

        System.out.println(bb);
    }

}
