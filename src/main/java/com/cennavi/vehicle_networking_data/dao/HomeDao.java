package com.cennavi.vehicle_networking_data.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cennavi.vehicle_networking_data.mercator.TileUtils;
import com.cennavi.vehicle_networking_data.utils.*;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import no.ecc.vectortile.VectorTileEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by 60195 on 2019/9/18.
 */
@Component
public class HomeDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @Value("${params.tableName_realtime}")
    String tableName_realtime;

    @Value("${params.onlineTime}")
    int onlineTime;

    @Value("${params.backTime}")
    int backTime;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public byte[] realtimeScatter(int x,int y, int z){
        VectorTileEncoder encoder = null;

        long currtime = System.currentTimeMillis();   //毫秒转秒
        String curtime = TimeUtil.timeChange(currtime);
        String strwkt = TileUtils.parseXyz2Bound(x, y, z);

        String sql = "select a.cp_hm,sd, a.gps_sj, st_astext(st_geomfromtext('Point('||a.jd||' '||a.wd||')',4326)) as geom,a.bgroup_id from gps_ss a,vehicle_manage b where a.gps_sj<? and" +
                " a.cp_hm=b.license_plate AND b.online_status=1 AND st_intersects(st_geomfromtext('Point('||a.jd||' '||a.wd||')',4326),st_geomfromtext(?,4326))";

        List<Map<String,Object>> list = jdbcTemplate.queryForList(sql,curtime,strwkt);

        encoder = new VectorTileEncoder(4096, 16, false);
        for(Map<String,Object> map : list){
            Map<String, Object> attr = new HashMap<String, Object>();
            WKTReader wktReader = new WKTReader();
            try {
                Geometry geom = wktReader.read(map.get("geom").toString());
                TileUtils.convert2Piexl(x, y, z, geom);

            long gps_time = TimeUtil.changeTime(map.get("gps_sj").toString());
            long timeDiff = TimeUtil.timeDiff(currtime,gps_time);
            if(timeDiff <= onlineTime){
                attr.put("active",1);
            }else {
                attr.put("active",0);
            }
            attr.put("cph",map.get("cp_hm"));
            attr.put("sd",map.get("sd"));
            attr.put("time",map.get("gps_sj"));
            attr.put("type",map.get("bgroup_id"));

            encoder.addFeature("carsdot", attr, geom);

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }
        return encoder.encode();
    }

    public byte[] realtimeScatterByCondition(int x,int y, int z, String business, String group){
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(d);

        VectorTileEncoder encoder = null;

        String busi[] = business.split(",");
        String gro[] = group.split(",");
        String busiSql = "";
        String groupSql = "";
        if(!business.equals("0")){    //如果不是全部的业务组
            if(business.contains(",")){
                busiSql = "AND bgroup_id in (";
                for(int i=0; i<busi.length; i++){
                    busiSql += busi[i]+",";
                }
                busiSql = busiSql.substring(0,busiSql.length()-1)+")";
            }else {
                busiSql = "AND bgroup_id in ("+busi[0]+")";
            }
        }

        if(!group.equals("0")){    //如果不是全部的车组
            if(group.contains(",")){
                    groupSql = " and cgroup_id in (";

                for(int i=0; i<gro.length; i++){
                    groupSql += gro[i]+",";
                }
                groupSql = groupSql.substring(0,groupSql.length()-1)+")";
            }else {
                groupSql = " and cgroup_id in ("+busi[0]+")";

            }
        }
        String strwkt = TileUtils.parseXyz2Bound(x, y, z);
        String sql = "with a1 as ( " +
                "SELECT license_plate FROM vehicle_manage WHERE online_status =1" + busiSql + groupSql +
                ") " +
                "SELECT cp_hm,sd, gps_sj, st_astext(st_geomfromtext('Point('||jd||' '||wd||')',4326)) as geom,bgroup_id from gps_ss a, a1 WHERE a.cp_hm=a1.license_plate and gps_sj<? and "
                + " st_intersects(st_geomfromtext('Point('||jd||' '||wd||')',4326),st_geomfromtext(?,4326))";
        List<Map<String,Object>> list = jdbcTemplate.queryForList(sql,currentTime,strwkt);

        encoder = new VectorTileEncoder(4096, 16, false);
        for(Map<String,Object> map : list){
            Map<String, Object> attr = new HashMap<String, Object>();
            WKTReader wktReader = new WKTReader();
            try {
                Geometry geom = wktReader.read(map.get("geom").toString());
                TileUtils.convert2Piexl(x, y, z, geom);

                long timeDiff = TimeUtil.calTime(currentTime,map.get("gps_sj").toString());
                if(timeDiff <= onlineTime){
                    attr.put("active",1);
                }else {
                    attr.put("active",0);
                }
                attr.put("cph",map.get("cp_hm"));
                attr.put("sd",map.get("sd"));
                attr.put("time",map.get("gps_sj"));
                attr.put("type",map.get("bgroup_id"));

                encoder.addFeature("carsdot", attr, geom);

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return encoder.encode();
    }

    public byte[] heatMap(int z, int x, int y){
        VectorTileEncoder encoder = null;

        long currtime = System.currentTimeMillis();   //毫秒转秒
        String curtime = TimeUtil.timeChange(currtime);
        String strwkt = TileUtils.parseXyz2Bound(x, y, z);

        String sql = "select cp_hm,sd, gps_sj, st_astext(st_geomfromtext('Point('||jd||' '||wd||')',4326)) as geom,bgroup_id  from gps_ss where gps_sj<? and" +
                " st_intersects(st_geomfromtext('Point('||jd||' '||wd||')',4326),st_geomfromtext(?,4326))";

        List<Map<String,Object>> list = jdbcTemplate.queryForList(sql,curtime,strwkt);

        encoder = new VectorTileEncoder(4096, 16, false);
        for(Map<String,Object> map : list){
            Map<String, Object> attr = new HashMap<String, Object>();
            WKTReader wktReader = new WKTReader();
            try {
                Geometry geom = wktReader.read(map.get("geom").toString());
                TileUtils.convert2Piexl(x, y, z, geom);

                long gps_time = TimeUtil.changeTime(map.get("gps_sj").toString());
                long timeDiff = TimeUtil.timeDiff(currtime,gps_time);
                if(timeDiff <= onlineTime){
                    attr.put("active",1);
                }else {
                    attr.put("active",0);
                }
                attr.put("cph",map.get("cp_hm"));
                attr.put("sd",map.get("sd"));
                attr.put("time",map.get("gps_sj"));
                attr.put("type",map.get("bgroup_id"));

                encoder.addFeature("heatMap", attr, geom);

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }
        return encoder.encode();

    }

    int index = 1;

    public JSONObject realtimeTracking(String cph, String time, int group){
        List<Long> listTime = new ArrayList<Long>();
        List<Integer> listSpeed = new ArrayList<Integer>();
        List<String> listPoint = new ArrayList<String>();
        List<Short> listHeading = new ArrayList<Short>();
        long current = System.currentTimeMillis();
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(d);
      //  String lastTime = TimeUtil.timeChange(time);
        //根据时间判断查哪个表 格式：2019-09-20 10:08:08
        String year = time.substring(0,4);
        String mounth = time.substring(5,7);
        String tableName;
        if(group == 1){
            tableName = "zf_gps_ls";
        }else if(group == 2){
            tableName = "lh_gps_ls";
        }else {
            tableName = "hw_gps_ls";
        }
        //如果是从首页进入，当前时间不是上一个gps点的时间，需要在当前时间下往后倒推几分钟
        long timeDiff = TimeUtil.calTime(currentTime,time);
        if(timeDiff<1){
            time = TimeUtil.getTimeByMinute(-backTime);
        }

        String sql = "select gps_sj,sd,jd,wd, cp_hm,fx from "+tableName+ " where cp_hm=? and gps_sj>=? order by gps_sj limit "+ 4;
        index++;
        System.out.println(sql);
        List<Map<String,Object>> list = new ArrayList<>();

        list = jdbcTemplate.queryForList(sql,cph,time);

        try{
            for(Map<String,Object> map : list){
                String spoint = map.get("jd")+ " " + map.get("wd");
                listTime.add(TimeUtil.changeTime(map.get("gps_sj").toString()));
                listSpeed.add((int)Math.floor(Double.parseDouble(map.get("sd").toString())));
                listHeading.add(Short.parseShort(map.get("fx").toString()));
                listPoint.add(spoint);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        // 1:如果传入的参数是一辆非活跃车ID,
        // 2:半小时内车辆没有行驶过
        // 则处理为返回一个空对象
        if(listTime.size() == 0) {
            return new JSONObject();
        }

        //倒序
        Collections.reverse(listTime);
        Collections.reverse(listSpeed);
        Collections.reverse(listPoint);
        Collections.reverse(listHeading);

        //检查计算两个点的距离是否特别大(120000/3600*60=2000)
        checkAvailablePoint(listTime, listSpeed, listPoint);

        JSONObject obj = null;
        //分段：每分钟60个点
        log.info("60-segment --- listPoint.size=" + listPoint.size() + ", [0]:"+listPoint.get(0) + ", ["+(listPoint.size()-1)+"]:"+listPoint.get(listPoint.size()-1));
        //把参数中传过来的坐标作为起点插入到list中
//        listTime.add(0, listTime.get(0)-10*1000);
//        listSpeed.add(0, 0);
        obj = gen60SegmentCroodnate(listTime, listSpeed, listPoint);

        return obj;

    }



    // 计算两个时间差，返回为分钟。
    private long CalTime(String time1, String time2) {
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

    //判断两个点是否很远
    private void checkAvailablePoint(List<Long> listTime, List<Integer> listSpeed, List<String> listPoint) {
        if(listPoint.size() > 1) {
            String[] lonlat1 = listPoint.get(0).split(" ");
            double lon1 = Double.parseDouble(lonlat1[0]);
            double lat1 = Double.parseDouble(lonlat1[1]);

            String[] lonlat2 = listPoint.get(1).split(" ");
            double lon2 = Double.parseDouble(lonlat2[0]);
            double lat2 = Double.parseDouble(lonlat2[1]);

            double distance = AdminUtil.distance(lat1, lon1, lat2, lon2);
            if(distance > 2000) {
                listTime.remove(0);
                listSpeed.remove(0);
                listPoint.remove(0);

            }
        }
    }

    //补点，产生60个点
    private JSONObject gen60SegmentCroodnate(List<Long> listTime, List<Integer> listSpeed, List<String> listPoint) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuffer sb = new StringBuffer();
        JSONArray arr = new JSONArray();
        JSONObject rootObj = new JSONObject();

        int size = listTime.size();

        try {
            //1: 考虑到从数据库中本身就查出一条记录
            if(size == 1) {
                String[] lonlat1 = listPoint.get(0).split(" ");
                double lng1 = Double.parseDouble(lonlat1[0]);
                double lat1 = Double.parseDouble(lonlat1[1]);
                JSONObject startObj = new JSONObject();
                //startObj.put("speed", listSpeed.get(0));
                startObj.put("speed", 0); //同一个位置速度应该为0
                startObj.put("lng", lng1);
                startObj.put("lat", lat1);
                //startObj.put("status", SpeedOptions.getOverSpeedScale(0));//同一个位置状态也应该为0
                startObj.put("status", 1);
                arr.add(startObj);
                sb.append("[" + lng1 + "," + lat1 + "]");
                rootObj.put("updatetime", listTime.get(0));
                rootObj.put("etime", sdf.format(new Date(listTime.get(0))));
                rootObj.put("data", arr);
                rootObj.put("lastpoint", lng1+","+lat1);
            }else {
                List<Long> times = new ArrayList<Long>();
                List<Integer> speeds = new ArrayList<Integer>();
                List<String> points = new ArrayList<String>();
                times.add(listTime.get(0));
                speeds.add(listSpeed.get(0));
                points.add(listPoint.get(0));
                int idx = -1;
                String lastPoint = listPoint.get(0);
                for(int i=0; i<size; i++) {
                    String tmpPoint = listPoint.get(i);
                    if(!lastPoint.equals(tmpPoint)) {
                        idx = i;	//记住位置
                        times.add(listTime.get(i));
                        speeds.add(listSpeed.get(i));
                        points.add(listPoint.get(i));
                    }
                    lastPoint = tmpPoint;
                }

                int msize = times.size();
                //2: 比较位置后，发现是同一个位置
                if(msize == 1) {
                    String[] lonlat1 = listPoint.get(0).split(" ");
                    double lng1 = Double.parseDouble(lonlat1[0]);
                    double lat1 = Double.parseDouble(lonlat1[1]);
                    JSONObject startObj = new JSONObject();
                    // 修改速度
                    //startObj.put("speed", listSpeed.get(0));
                    startObj.put("speed", 0); //同一个位置速度应该为0
                    //startObj.put("speed", listSpeed.get(0));
                    startObj.put("lng", lng1);
                    startObj.put("lat", lat1);
                    //startObj.put("status", SpeedOptions.getOverSpeedScale(0));//同一个位置状态也应该为0
                    startObj.put("status", 1);
//                    arr.add(startObj);
                    sb.append("[" + lng1 + "," + lat1 + "]");
                    rootObj.put("updatetime", listTime.get(0));
                    rootObj.put("etime", sdf.format(new Date(listTime.get(size-1))));
                    rootObj.put("data", arr);
                    rootObj.put("lastpoint", lng1+","+lat1);
                }else {
                    //匹配轨迹
//                    JSONArray matchArr = MatchTrackingUtil.trackingMatch(points, times);
                    JSONArray matchArr = MatchTrackingNewUtil.trackingMatch(points, times);
//                    JSONArray matchArr = MatchTrackingUtilNew.trackingMatch(points, times);
                    if(matchArr == null) {
                        //再请求一遍（这里有可能访问的轨迹匹配服务出现错误）
                        log.info("request-again [60-segment] ...");
                        matchArr = MatchTrackingNewUtil.trackingMatch(points, times);
                    }
                    //如果还是空的话,那么取最后一个点作为当前汽车的位置
                    if(matchArr==null || matchArr.size()==0) {
                        log.info("end-null [60-segment] ...");
                        String[] lonlat2 = listPoint.get(size-1).split(" ");
                        double lng2 = Double.parseDouble(lonlat2[0]);
                        double lat2 = Double.parseDouble(lonlat2[1]);
                        JSONObject endObj = new JSONObject();
                        // 修改速度
                        //startObj.put("speed", listSpeed.get(0));
                        endObj.put("speed", 0); //取最后一个位置速度应该为0
                        //startObj.put("speed", listSpeed.get(0));
                        endObj.put("lng", lng2);
                        endObj.put("lat", lat2);
                        //endObj.put("status", SpeedOptions.getOverSpeedScale(0));//取最后一个位置状态也应该为0
                        endObj.put("status", 1);
                        arr.add(endObj);
                        sb.append("[" + lng2 + "," + lat2 + "]");
                        rootObj.put("updatetime", listTime.get(size-1));
                        rootObj.put("etime", sdf.format(new Date(listTime.get(size-1))));
                        rootObj.put("data", arr);
                        rootObj.put("lastpoint", lng2+","+lat2);
                        return rootObj;
                    }
                    log.info("success [60-segment] ...");

                    //总共有几段
                    int duan = 0;
                    if(matchArr.size() == 1) {
                        duan = 1;
                    }else {
                        duan = matchArr.size() - 1;
                    }

                    //每段可以分到几个比例值
                    int biLi = 60 / duan;

//                    for(int j=0; j<duan-1; j++) {        //旧版
                    for(int j=0; j<duan; j++) {
                        JSONArray jarr1 = (JSONArray)matchArr.get(j);
                        double lng1 = (double)jarr1.get(0);
                        double lat1 = (double)jarr1.get(1);
                        int speed = (int)jarr1.get(2);
                        JSONArray jarr2 = (JSONArray)matchArr.get(j+1);
                        double lng2 = (double)jarr2.get(0);
                        double lat2 = (double)jarr2.get(1);
                        double lonDvalue = lng2 - lng1;
                        double latDvalue = lat2 - lat1;
                        double lonSegment = lonDvalue / biLi;
                        double latSegment = latDvalue / biLi;

                        JSONObject startObj = new JSONObject();
                        startObj.put("speed", speed);
                        startObj.put("lng", lng1);
                        startObj.put("lat", lat1);
                        //startObj.put("status", SpeedOptions.getOverSpeedScale(speed));
                        startObj.put("status", 1);
//                        arr.add(startObj);
                        sb.append("[" + lng1 + "," + lat1 + "],");

                        for(int x = 0; x < biLi; x++) {
                            JSONObject midObj = new JSONObject();
                            double tmpLng = lng1 + lonSegment;
                            double tmpLat = lat1 + latSegment;
                            //构造方法的字符格式这里如果小数不足6位,会以0补足.
                            DecimalFormat decimalFormat = new DecimalFormat(".000000");
                            String sTmpLng = decimalFormat.format(tmpLng);
                            String sTmpLat = decimalFormat.format(tmpLat);
                            midObj.put("lng", sTmpLng);
                            midObj.put("lat", sTmpLat);
                            // 重新计算速度
                            //double distance = AdminUtil.distance(lat1, lng1, tmpLat, tmpLng);
                            //int speedTmp = (int)(distance / 1.0 * 3.6);
                            //midObj.put("speed", speedTmp);
                            midObj.put("speed", speed);
                            //midObj.put("status", SpeedOptions.getOverSpeedScale(speed));
                            midObj.put("status", 1);
                            arr.add(midObj);
                            sb.append("[" + sTmpLng + "," + sTmpLat + "],");

                            //更新中间变量
                            lng1 = tmpLng;
                            lat1 = tmpLat;

                            //更新最后一个点
                            rootObj.put("lastpoint", tmpLng+","+tmpLat);
                        }
                    }

//                    rootObj.put("updatetime", times.get(msize-1));
//                    rootObj.put("etime", sdf.format(new Date(times.get(msize-1))));
                    rootObj.put("updatetime", times.get(0));
                    rootObj.put("etime", sdf.format(new Date(times.get(0))));
                    rootObj.put("data", arr);


                }
            }
        }catch(Exception e) {
            log.error("60-segment-error:", e);
        }
        //System.out.println("end: " + sb.toString());
        return rootObj;
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
