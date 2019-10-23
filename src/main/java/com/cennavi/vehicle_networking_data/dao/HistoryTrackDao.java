package com.cennavi.vehicle_networking_data.dao;
 
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cennavi.vehicle_networking_data.utils.MatchTrackingNewUtil;
import com.cennavi.vehicle_networking_data.utils.TimeUtil;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cennavi.vehicle_networking_data.utils.TimeUtil.calTime;

/**
 * Created by 60195 on 2019/10/15.
 */
@Component
public class HistoryTrackDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${params.travelInterval}")
    int travelInterval;

    public Object getHistoryTrack(String key, String sTime, String eTime, int type, int isOwn ){
        //根据时间判断查哪个表 格式：2019-09-20 10:08:08

        String tableName;
        //判断车辆属于哪个业务组
        String sql1;
        if(type == 1){
            sql1 = "SELECT bgroup_id FROM vehicle_manage WHERE license_plate=?";
        }else {
            sql1 = "SELECT bgroup_id FROM vehicle_manage WHERE car_id=?";
        }
        List<Map<String,Object>> groupList = jdbcTemplate.queryForList(sql1,key);
        if(groupList.size() < 1){
            return null;
        }
        int busi_id = (int) groupList.get(0).get("bgroup_id");
        if(busi_id == 1){
            tableName = "zf_gps_ls";
        }else if(busi_id == 2){
            tableName = "lh_gps_ls";
        }else {
            tableName = "hw_gps_ls";
        }

        //查找对应时间段内的轨迹数据
        String sql2;
        if(type == 1){
            sql2 = "SELECT jd,wd,sd,gps_sj,fx FROM "+tableName+" WHERE cp_hm=? AND gps_sj BETWEEN ? AND ? ORDER BY gps_sj ";
        }else {
            sql2 = "SELECT jd,wd,sd,gps_sj,fx FROM "+tableName+" WHERE rfid_id=? AND gps_sj BETWEEN ? AND ? ORDER BY gps_sj ";
        }

        List<Map<String,Object>> gpsList = jdbcTemplate.queryForList(sql2,key,sTime,eTime);
        if(gpsList.size()<2){
            return null;
        }
        //划分行程，很重要，按行程去请求推测接口
        List<List<Map<String,Object>>> travelList =  partTravel(gpsList);

        //调用推测接口去获取每段行程的轨迹
        List<Object> tracks = new ArrayList<>();
        for(List<Map<String,Object>> list: travelList){
            //除去少量的某些点组成的轨迹
            if(list.size()<=5){
                continue;
            }
            List<Long> listTime = new ArrayList<Long>();
            List<Integer> listSpeed = new ArrayList<Integer>();
            List<String> listPoint = new ArrayList<String>();
            List<Short> listHeading = new ArrayList<Short>();
            String startTime = list.get(0).get("gps_sj").toString();
            String endTime = list.get(list.size()-1).get("gps_sj").toString();
            String time = startTime+ " - " + endTime;

            try{
                for(Map<String,Object> map : gpsList){
                    String spoint = map.get("jd")+ " " + map.get("wd");
                    listTime.add(TimeUtil.changeTime(map.get("gps_sj").toString()));
                    listSpeed.add((int)Math.floor(Double.parseDouble(map.get("sd").toString())));
                    listHeading.add(Short.parseShort(map.get("fx").toString()));
                    listPoint.add(spoint);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            JSONObject obj = getLinkShapesCroodnate(listTime,listSpeed,listPoint,listHeading,key,isOwn);
            obj.put("time",time);
            obj.put("car_num",key);
            tracks.add(obj);
        }

        return tracks;
    }

    /**
     *  将排序好的原始gps信息划分行程
     * @param gpsList1
     * @return
     */
    //逻辑上没发现毛病，但是debug不是想要的结果
    List<List<Map<String,Object>>> travelList1 = new ArrayList<>(); //所有的行程
    private void getTravel(List<Map<String,Object>> gpsList1){
        List<Map<String,Object>> travelListCopy = gpsList1;
        int size = travelListCopy.size();
        List<Map<String,Object>> travelListSub = new ArrayList<>();
        for(int i=0; i<size-1; i++){
            String time1 = travelListCopy.get(i).get("gps_sj").toString();
            String time2 = travelListCopy.get(i+1).get("gps_sj").toString();
            long timeDiff = calTime(time1,time2);
            if(timeDiff < travelInterval){
                travelListSub.add(travelListCopy.get(i));
            }else {
                travelListSub.add(travelListCopy.get(i));
                travelList1.add(travelListSub);
                //对剩下的点递归调用该方法继续分割行程
                List<Map<String,Object>> travelListNext = travelListCopy.subList(i+1,size);
                getTravel(travelListNext);
            }
        }
    }

    private List<List<Map<String,Object>>> partTravel(List<Map<String,Object>> gpsList){
        List<List<Map<String,Object>>> travelList = new ArrayList<>(); //所有的行程

        //保存行程的分界点
        List<Integer> indexList = new ArrayList<>();
     //   indexList.add(0);
        for(int i=0; i<gpsList.size()-1; i++){       //划分行程
            String time1 = gpsList.get(i).get("gps_sj").toString();
            String time2 = gpsList.get(i+1).get("gps_sj").toString();
            long timeDiff = calTime(time1,time2);
            if(timeDiff<travelInterval){     //gps点时间小于x分钟的都算一次行驶过程
                continue;
            }else {
                indexList.add(i+1);       //记住行程的分界点
//                indexList.add(i+1);      //确保是双数
            }
        }
    //如果没有分界点，只有一段行程
        if(indexList.size() == 0){
            travelList.add(gpsList);
            return travelList;
        }
        //从分界点将每段行程添加进行程表
        for(int j=0; j<=indexList.size(); j++){
           //第一段行程
            if(j==0){
                travelList.add(gpsList.subList(0,indexList.get(j)));
                continue;
            }
            //最后一段行程
            if(j == indexList.size()){
                travelList.add(gpsList.subList(indexList.get(j-1),gpsList.size()));
                break;
            }
            //中间行程
            travelList.add(gpsList.subList(indexList.get(j-1),indexList.get(j)));
        }
        return travelList;
    }

    /**
     * 获取道路形状点
     */
    private JSONObject getLinkShapesCroodnate(List<Long> listTime, List<Integer> listSpeed, List<String> listPoint,List<Short> listHeading,String key, int isOwn) {
        JSONObject tracks = new JSONObject();
        JSONArray matchArr;
        if(isOwn == 1){
            tracks = MatchTrackingNewUtil.trackingMatchHistoryOwn(listTime, listSpeed,listPoint,listHeading,key);
            JSONArray subPointsArr = tracks.getJSONArray("subPoints");
            //找出超速点
            int limitSpeed = (int) tracks.get("limitSpeed");
            //数据中有速度值的情况
            List<Object> overSpeedList = new ArrayList<>();
            for(int i=0; i<listSpeed.size(); i++){
                if(listSpeed.get(i) > limitSpeed){
                    Map<String,Object> overSpeedMap = new HashMap<>();
                    JSONObject subPoint = subPointsArr.getJSONObject(i);
                    if(subPoint.getString("lon") != null){
                        double location[] = new double[2];
                        location[0] = subPoint.getDouble("lon");
                        location[1] = subPoint.getDouble("lat");
                        overSpeedMap.put("location",location);
                        overSpeedMap.put("time",TimeUtil.timeChange(listTime.get(i)));
                        overSpeedMap.put("speed",listSpeed.get(i)+"km/h");
                        overSpeedMap.put("limitSpeed",limitSpeed+"km/h");
                        overSpeedList.add(overSpeedMap);
                    }
                }
            }
            tracks.put("overSpeedList",overSpeedList);

            //找出疲劳驾驶点
            String longTime = "";
            List<Object> fatigueDrivingList = new ArrayList<>();
            //20s一个点，3个小时就是540个点才会出现疲劳驾驶
            for(int j=500; j<subPointsArr.size(); j++){
                long firstTime = listTime.get(0);
                JSONObject lastPoint = subPointsArr.getJSONObject(subPointsArr.size()-1);
                JSONObject subPoint = subPointsArr.getJSONObject(j);
                long newTime = subPoint.getLong("gpstime");
                String duration = (lastPoint.getLong("gpstime") - firstTime)/3600.0 + "h";
                if((newTime - firstTime)/3600.0 > 3.0){
                    if(subPoint.getString("lon") != null){
                        Map<String,Object> fatigueDrivingMap = new HashMap<>();
                        double location[] = new double[2];
                        location[0] = subPoint.getDouble("lon");
                        location[1] = subPoint.getDouble("lat");
                        fatigueDrivingMap.put("location",location);
                        fatigueDrivingMap.put("time",TimeUtil.timeChange(listTime.get(j)));
                        fatigueDrivingMap.put("duration",duration);
                        fatigueDrivingList.add(fatigueDrivingMap);
                    }
                }
            }
            tracks.put("fatigueDriving",fatigueDrivingList);

        }else {
            matchArr = MatchTrackingNewUtil.trackingMatchHistoryCustomer(listTime, listSpeed,listPoint,listHeading,key);
            tracks.put("tracks",matchArr);
        }
        tracks.remove("subPoints");
        return  tracks;
    }


}
