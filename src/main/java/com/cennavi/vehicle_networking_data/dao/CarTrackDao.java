package com.cennavi.vehicle_networking_data.dao;

import com.alibaba.fastjson.JSONObject;
import com.cennavi.vehicle_networking_data.utils.HttpRequestUtil;
import com.cennavi.vehicle_networking_data.utils.TimeUtil;

//import com.cennavi.utils.HttpRequestUtil;
//import com.cennavi.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.awt.print.PrinterGraphics;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by 60195 on 2019/9/23.
 */
@Component
public class CarTrackDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${params.travelInterval}")
    int travelInterval;

    @Value("${params.onlineTime}")
    int onlineTime;

    @Value("${params.reversCodingUrl}")
    String reversCodingUrl;

    public Map<String,Object> getCarMsg(String cph, String date, int type){
        Map<String,Object> resultMap = new HashMap<>();

        String tableName;
        if(type == 1){   //执法
            tableName = "zf_gps_ls";
        }else if(type == 2){  //绿化
            tableName = "lh_gps_ls";
        }else {  //环卫
            tableName = "hw_gps_ls";
        }
        //查询在线时间
        String sql1 = "SELECT cp_hm,gps_sj FROM "+ tableName+" WHERE cp_hm=? and gps_sj like '"+date.substring(0,11)+"%' ORDER BY gps_sj ASC";
        List<Map<String,Object>> timeList = jdbcTemplate.queryForList(sql1,cph);

        String totalTime = "";    //在线总时长
        //保存行程的分界点
        List<Integer> indexList = new ArrayList<>();
        indexList.add(0);
        for(int i=0; i<timeList.size()-1; i++){       //划分行程
            String time1 = timeList.get(i).get("gps_sj").toString();
            String time2 = timeList.get(i+1).get("gps_sj").toString();
            long timeDiff = calTime(time1,time2);
            if(timeDiff<travelInterval){     //gps点时间小于5分钟的都算一次行驶过程
                continue;
            }else {
                indexList.add(i);       //记住行程的分界点
                indexList.add(i+1);      //确保是双数
            }
        }
        indexList.add(timeList.size()-1);
        //根据分界点计算每个行程的时长
        long totalMin = 0;
        if(timeList.size() != 0){
            for(int j=0; j<indexList.size()-1; j=j+2){
                totalMin += calTime(timeList.get(indexList.get(j)).get("gps_sj").toString(),timeList.get(indexList.get(j+1)).get("gps_sj").toString());
            }
        }

        totalTime = (int) Math.floor(totalMin / 60) + "h" + totalMin%60 + "min";

        //作业里程
        String sql2 = "SELECT avg(to_number(sd, '99.99')) as avg_speed FROM "+ tableName+" WHERE cp_hm=? and gps_sj like '"+date.substring(0,11)+"%'";
        Map<String,Object> speedMap = jdbcTemplate.queryForMap(sql2,cph);
        double mileage = 0;
        if(speedMap.get("avg_speed") != null){
           mileage = Double.parseDouble(speedMap.get("avg_speed").toString()) * totalMin/60.0;
        }

        //当前车辆实时位置
        String sql4 = "SELECT jd,wd,gps_sj FROM gps_ss WHERE cp_hm=? limit 1";
        List<Map<String,Object>> locationList = jdbcTemplate.queryForList(sql4,cph);
        Map<String,Object> locationMap = null;
        if(locationList.size()>0){
            locationMap = locationList.get(0);
        }else {

        }

//        String url = "http://117.48.214.8:8001/service/coder/reverseGeocoding";
        String params;
        String address ="";
        if(locationMap == null){

        }else {
            String location = locationMap.get("jd").toString()+ ","+ locationMap.get("wd").toString();
            params = "token=11&"+ "location="+ location;
            //逆地理编码获取位置信息
            String result = HttpRequestUtil.sendGet(reversCodingUrl,params);
            JSONObject data = JSONObject.parseObject(result);
            System.out.println(data.toJSONString());
            if(data != null && data.getString("data") != null){
                JSONObject resultJson = JSONObject.parseObject(data.getString("data"));
                /*if(resultJson.get("roadname") == null || resultJson.get("roadname").equals("")){
                    if(resultJson.get("poiAddr") == null || resultJson.get("poiAddr").equals("")){
                        if(resultJson.get("poiName") == null || resultJson.get("poiName").equals("")){

                        }else {
                            address = resultJson.get("poiName").toString();
                        }
                    }else {
                        address = resultJson.get("poiAddr").toString();
                    }
                }else {
                    address = resultJson.get("roadname").toString();
                }*/
                if(resultJson.get("roadname") == null || resultJson.get("roadname").equals("")){
                    if(resultJson.get("address") == null || resultJson.get("address").equals("")){
                        if(resultJson.get("poi") == null || resultJson.get("poi").equals("")){

                        }else {
                            address = resultJson.get("poi").toString();
                        }
                    }else {
                        address = resultJson.get("address").toString();
                    }
                }else {
                    address = resultJson.get("roadname").toString();
                }
            }
        }

        //其他信息
        String sql3 = "SELECT a.*,b.name business,c.name car_group FROM vehicle_manage a,business_group b,car_group_manage c WHERE a.license_plate=? AND a.bgroup_id=b.id AND a.cgroup_id=c.id AND b.id=c.bgroup_id";
        List<Map<String,Object>> infoList = jdbcTemplate.queryForList(sql3,cph);
        if(infoList.size() == 0){
            return resultMap;
        }
        mileage = new BigDecimal(mileage).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        resultMap = infoList.get(0);
        resultMap.put("total_time", totalTime);
        resultMap.put("total_mileage",mileage);
        resultMap.put("update_time",locationMap.get("gps_sj"));
        resultMap.put("location",address);
        return resultMap;
    }

    /**
     * 车辆简略信息
     * @param cph
     * @return
     */
    public Map<String,Object> carSimpleInfo(String cph){
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(d);

        Map<String,Object> resultMap = new HashMap<>();

        String sql1 = "SELECT b.name business,c.name car_group,b.id FROM vehicle_manage a,business_group b,car_group_manage c WHERE a.license_plate=? AND a.bgroup_id=b.id AND a.cgroup_id=c.id AND b.id=c.bgroup_id";
        List<Map<String,Object>> infoList = jdbcTemplate.queryForList(sql1,cph);
        if(infoList.size() == 0){
            return resultMap;
        }
        resultMap.put("business",infoList.get(0).get("business"));
        resultMap.put("car_group",infoList.get(0).get("car_group"));
        resultMap.put("type",Integer.parseInt(infoList.get(0).get("id").toString()));

        String sql2 = "SELECT DISTINCT a.gps_sj from gps_ss a,vehicle_manage b WHERE cp_hm=? AND online_status=1 AND a.cp_hm=b.license_plate";
        Map<String,Object> timeMap = jdbcTemplate.queryForMap(sql2,cph);
        if(timeMap==null){
            resultMap.put("active",0);
            return resultMap;
        }
        if(timeMap.get("gps_sj")==null){
            resultMap.put("active",0);
            return resultMap;
        }
        long timeDiff = TimeUtil.calTime(currentTime,timeMap.get("gps_sj").toString());
        if(timeDiff <= onlineTime){
            resultMap.put("active",1);
        }else {
            resultMap.put("active",0);
        }
        return  resultMap;
    }

    // 计算两个时间差，返回为分钟。
    private long calTime(String time1, String time2) {
        DateFormat df = new SimpleDateFormat("YYYY-MM-DD HH:mm:SS");
        long minutes = 0L;
        try {
            Date d1 = df.parse(time1);
            Date d2 = df.parse(time2);
            minutes = Math.abs((d1.getTime() - d2.getTime()) / (1000 * 60));
        } catch (java.text.ParseException e) {
            System.out.println("时间日期解析出错。");
        }
        return minutes;
    }
}
