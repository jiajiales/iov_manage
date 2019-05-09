package com.cennavi.audi_data_collect.dao;



import com.cennavi.audi_data_collect.bean.ParamsBean;
import com.cennavi.audi_data_collect.util.TileUtils;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import no.ecc.vectortile.VectorTileEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cennavi on 2019/4/29.
 */
@Component
public class HomeDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String,Object>> getRoad(){
        return null;
    }

    /**
     * 返回每个segment的id
     */
    public byte[] getSegmentId(int x,int y, int z) throws Exception{
        String tile = TileUtils.parseXyz2Bound(x, y, z);

        String sql = "SELECT st_astext(geom) geom,id,road_id FROM gaosu_segment WHERE st_intersects(geom, st_geometryfromtext('"+tile+"', 4326))=true ";
        List<Map<String,Object>> idList = jdbcTemplate.queryForList(sql);
        VectorTileEncoder vte = new VectorTileEncoder(4096, 16, false);
        for (Map<String, Object> m : idList) {
            Map<String,Object> idMap = new HashMap<>();
            idMap.put("id",m.get("id"));
            idMap.put("road_id",m.get("road_id"));

            String wkt = (String) m.get("geom");

            Geometry geom = new WKTReader().read(wkt);

            TileUtils.convert2Piexl(x, y, z, geom);

            vte.addFeature("geom", idMap, geom);
        }
        return vte.encode();
    }

    /**
     * 行人热力图
     */
    public byte[] getVRUEvent(int x,int y,int z,String sDate,String eDate,String dates,String sTime,String eTime,int orderType,String road) throws Exception{
        String tile = TileUtils.parseXyz2Bound(x, y, z);
        String roadSql = getRoadSql(road);
        String dataSql = getDataSql(dates);

        String sql1 = "SELECT st_astext(geom) as geom,id FROM gaosu_segment WHERE "+ roadSql +" AND st_intersects(geom, st_geometryfromtext('"+tile+"', 4326))=true";
        List<Map<String,Object>> roadList = jdbcTemplate.queryForList(sql1);

        String sql2 = "SELECT b.id, count(a.event_id) FROM collection_info_new as a, gaosu_segment as b WHERE ST_DWithin(st_transform(a.geom,4527), st_transform(b.geom,4527), 20) " +
                "AND a.event_type='01' AND "+dataSql+" AND "+ dataSql +
                " AND st_intersects(geom, st_geometryfromtext('"+tile+"', 4326))=true GROUP BY b.id";
        List<Map<String,Object>> countList = jdbcTemplate.queryForList(sql2);

        VectorTileEncoder vte = new VectorTileEncoder(4096, 16, false);
        for (Map<String, Object> m : roadList) {
            Map<String,Object> idMap = new HashMap<>();
            idMap.put("proporties",idMap.get("id"));

            String wkt = (String) m.get("geom");

            Geometry geom = new WKTReader().read(wkt);

            TileUtils.convert2Piexl(x, y, z, geom);

            vte.addFeature("geom", idMap, geom);
        }
        Map<String,Object> countMap = new HashMap<>();
        countMap.put("count",countList);
        //vte.addFeature("count",countMap);

        return vte.encode();
    }

    /**
     * 选择单个路段查看信息
     * @param paramsBean
     * @return
     */
    public List<Map<String,Object>> getPointInfo(ParamsBean paramsBean){
        List<Map<String,Object>> resultList = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();

        String sql1 = "SELECT road_name FROM gaosu_segment WHERE id="+paramsBean.getRoadSecList()[0];
        List<Map<String,Object>> nameMap = jdbcTemplate.queryForList(sql1);
        if(nameMap.size()!=0){
            map.put("road",nameMap.get(0).get("road_name"));           //路名
        }
        String timeSql = " AND substring(a.time,12,16) BETWEEN '"+paramsBean.getTimeFrame()[0]+"' AND '"+paramsBean.getTimeFrame()[1]+"'";

        String sql2 = "";
        if(paramsBean.getIsContinuous().equals("true")){      //连续选择时间
           sql2 = "SELECT DISTINCT a.car_id FROM trip_rail as a, gaosu_segment as b WHERE b.id="+paramsBean.getRoadSecList()[0]+"  AND  " +
                    "ST_DWithin(st_transform(a.geom,4527), st_transform(b.geom,4527), 20) AND substring(a.time,1,10) BETWEEN '"+paramsBean.getDataList()[0]+"' AND '"+paramsBean.getDataList()[1]+"' " + timeSql;
        }else if(paramsBean.getIsContinuous().equals("false")){      //间隔选择时间
            sql2 = "SELECT DISTINCT a.car_id FROM trip_rail as a, gaosu_segment as b WHERE b.id="+paramsBean.getRoadSecList()[0]+"  AND  " +
                    "ST_DWithin(st_transform(a.geom,4527), st_transform(b.geom,4527), 20) AND "+getDataSql2(paramsBean.getDataList())+ timeSql;
        }
        List<Map<String,Object>> carList = jdbcTemplate.queryForList(sql2);     //有几辆车经过
        map.put("velchels",carList.size());

        int times = 0;   //经过的次数
        String sql3;

        if(carList == null || carList.size()==0){         //没有车经过
            map.put("times",carList.size());
            resultList.add(map);
        } else {
            for (Map<String,Object> carId: carList){     //每辆车经过几次
                String car_id = carId.get("car_id").toString();
                if(paramsBean.getIsContinuous().equals("true")){
                    sql3 = "SELECT a.time,a.lon,a.lat  FROM trip_rail as a, gaosu_segment as b WHERE b.id="+paramsBean.getRoadSecList()[0]+" AND " +
                            "ST_DWithin(st_transform(a.geom,4527), st_transform(b.geom,4527), 20) AND car_id='"+car_id+"'  AND substring(a.time,1,10) BETWEEN '"+paramsBean.getDataList()[0]+"' AND '"+paramsBean.getDataList()[1]+"' " + timeSql+" ORDER BY time ";
                }else {
                    sql3 = "SELECT a.time,a.lon,a.lat  FROM trip_rail as a, gaosu_segment as b WHERE b.id="+paramsBean.getRoadSecList()[0]+" AND " +
                            "ST_DWithin(st_transform(a.geom,4527), st_transform(b.geom,4527), 20) AND car_id='"+car_id+"' AND "+getDataSql2(paramsBean.getDataList())+ timeSql+"ORDER BY time ";
                }
                List<Map<String,Object>> infoList = jdbcTemplate.queryForList(sql3);

                if(infoList == null){
                    continue;
                } else if(infoList.size()==1){
                    times += 1;
                } else {
                    times += 1;
                    for(int i=0; i<infoList.size(); i++){             //从时间的连续性上看同一辆车经过几次
                        String time1 = infoList.get(i).get("time").toString().substring(11,16);       //截取小时和分钟
                        if ((i+1)<infoList.size()) {
                            String time2 = infoList.get(i + 1).get("time").toString().substring(11, 16);

                            int minute1 = Integer.parseInt(time1.substring(0, 2)) * 60 + Integer.parseInt(time1.substring(3));
                            int minute2 = Integer.parseInt(time2.substring(0, 2)) * 60 + Integer.parseInt(time2.substring(3));

                            if((minute2 - minute1)<60){      //如果是连续的一个小时内
                                continue;
                            } else {
                                times += 1;
                            }
                        }
                    }
                }
            }
            map.put("times",times);
            resultList.add(map);
        }
        return resultList;
    }

    //选多条路
    private String getRoadSql(String roads){
        String roadSql = " ";
        if(roads.contains(",")){
            String road[] = roads.split(",");
            roadSql = " en_name in (";
            for(int i=0; i<road.length; i++){
                roadSql = roadSql + "'" + road[i] + "'" + ",";
            }
            roadSql = roadSql.substring(0,roadSql.length()-1) + ")";
        }else {
            roadSql = " en_name in ('"+ roads + "')";
        }
        return roadSql;
    }

    //选多个日期
    private String getDataSql(String datas){
        String dataSql = " ";
        if(datas.contains(",")){
            String data[] = datas.split(",");
            dataSql = " substring(a.upload_time,1,10) in (";
            for(int i=0; i<data.length; i++){
                dataSql = dataSql + "'" + data[i] + "'" + ",";
            }
            dataSql = dataSql.substring(0,dataSql.length()-1) + ")";
        }else {
            dataSql = " substring(a.upload_time,1,10) in ('"+ datas + "')";
        }
        return dataSql;
    }

    private String getDataSql2(String[] dataList){
        String dataSql = "";
        for(int i=0; i<dataList.length; i++){
            dataSql = dataSql +"'"+dataList[i]+"'"+",";
        }
        dataSql = "substring(a.time,1,10) in ("+ dataSql.substring(0,dataSql.length()-1)+")";
//        if(datas.contains(",")){
//            String data[] = datas.split(",");
//            dataSql = " substring(a.time,1,10) in (";
//            for(int i=0; i<data.length; i++){
//                dataSql = dataSql + "'" + data[i] + "'" + ",";
//            }
//            dataSql = dataSql.substring(0,dataSql.length()-1) + ")";
//        }else {
//            dataSql = " substring(a.time,1,10) in ('"+ datas + "')";
//        }
        return dataSql;
    }

    //路段信息

}
