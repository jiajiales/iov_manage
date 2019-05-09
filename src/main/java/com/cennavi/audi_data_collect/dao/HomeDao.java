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
    public byte[] getVRUEvent(int x,int y,int z,ParamsBean paramsBean) throws Exception{
        String tile = TileUtils.parseXyz2Bound(x, y, z);
        String roadSql = getRoadSql(paramsBean.getRoadSecList());

        String roadSql2 = " (SELECT road_id FROM gaosu WHERE r_id in "+roadSql+")";
        String dataSql = getDataSql(paramsBean.getDataList());
        String timeSql = "  BETWEEN '"+paramsBean.getTimeFrame()[0]+"' AND '"+paramsBean.getTimeFrame()[1]+"'";

        String sql1 = "SELECT st_astext(geom) as geom,id,road_id FROM gaosu_segment a WHERE st_intersects(geom, st_geometryfromtext('"+tile+"', 4326))=true"+ " AND a.road_id in"+roadSql2 ;
        List<Map<String,Object>> roadList = jdbcTemplate.queryForList(sql1);

        String sql2;
        if(paramsBean.getIsContinuous().equals("true")) {      //连续选择时间

            sql2 = "with a1 as( " +
                    "SELECT count(b.id),segment_id FROM trip_segment_new as b, gaosu_segment as a " +
                    "WHERE b.segment_id=a.id AND a.road_id in " + roadSql2 + " AND substring(b.time,1,10) BETWEEN '"+paramsBean.getDataList()[0]+"' AND '"+paramsBean.getDataList()[1]+
                    "' AND substring(substring(b.time,12,16),1,5) " + timeSql +
                    " GROUP BY b.segment_id " +
                    "),a2 as("+
                    "SELECT st_astext(a.geom) as geom,a.id,count(b.event_id) FROM gaosu_segment as a,collection_info_new as b WHERE  " +
                    " b.event_type='01' and a.road_id in " + roadSql2 + " AND a.id=b.segment_id AND substring(b.upload_time,1,10) BETWEEN '"+paramsBean.getDataList()[0]+"' AND '"+paramsBean.getDataList()[1]+"' " +
                    " AND  substring(substring(b.upload_time,12,16),1,5)" + timeSql +
                    " GROUP BY a.id,a.geom )"+
                    "SELECT a2.geom,a2.id,a2.count*1.0/a1.count*1.0 as count FROM a1 join a2 ON a1.segment_id=a2.id WHERE 1=1";
        }else {      //间隔选择时间
            sql2 = "with a1 as( " +
                    "SELECT count(b.id),segment_id FROM trip_segment_new as b, gaosu_segment as a " +
                    "WHERE b.segment_id=a.id  AND a.road_id in" + roadSql2 + " and substring(b.time,1,10) in "+dataSql + " AND substring(substring(b.time,12,16),1,5)"+timeSql +
                    " GROUP BY b.segment_id " +
                    "),a2 as("+
                    "SELECT st_astext(a.geom) as geom,a.id,count(b.event_id) FROM gaosu_segment as a,collection_info_new as b WHERE  " +
                    " b.event_type='01' AND a.road_id in " + roadSql2 + " AND a.id=b.segment_id " +" AND substring(b.upload_time,1,10) in "+dataSql +" AND substring(substring(b.upload_time,12,16),1,5) "+ timeSql +
                    " GROUP BY a.id,a.geom )"+
                    "SELECT a2.geom,a2.id,a2.count*1.0/a1.count*1.0 as count FROM a1 join a2 ON a1.segment_id=a2.id WHERE 1=1";
        }
        List<Map<String,Object>> countList = jdbcTemplate.queryForList(sql2);

        VectorTileEncoder vte = new VectorTileEncoder(4096, 16, false);
        for (Map<String, Object> m : roadList) {
            Map<String,Object> idMap = new HashMap<>();
            idMap.put("segment_id",idMap.get("id"));
            idMap.put("road_id",idMap.get("rod_id"));

       //     Map<String,Object> countMap = new HashMap<>();
       //     idMap.put("count",countMap.get("id"));
            for(int i=0; i<countList.size(); i++){
                if(Integer.parseInt(m.get("id").toString()) == Integer.parseInt(countList.get(i).get("id").toString())){
                    idMap.put("count",countList.get(i).get("count"));
                    countList.remove(i);
                }
            }
            if(idMap.get("count") == null){
                idMap.put("count",0);
            }

            String wkt = (String) m.get("geom");

            Geometry geom = new WKTReader().read(wkt);

            TileUtils.convert2Piexl(x, y, z, geom);

            vte.addFeature("geom", idMap, geom);
        }

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

        String sql1 = "SELECT DISTINCT en_name FROM gaosu WHERE id="+ paramsBean.getRoadSecList()[0];
        List<Map<String,Object>> nameMap = jdbcTemplate.queryForList(sql1);
        if(nameMap.size()!=0){
            map.put("road",nameMap.get(0).get("road_name"));           //路名
        }
        String timeSql = " '"+paramsBean.getTimeFrame()[0]+"' AND '"+paramsBean.getTimeFrame()[1]+"'";

        String sql2 = "";
        if(paramsBean.getIsContinuous().equals("true")){      //连续选择时间
           sql2 = "SELECT count(DISTINCT car_id) as count1,count(DISTINCT id) as count2,segment_id FROM trip_segment_new WHERE segment_id in ("+paramsBean.getSegmentId()[0]+") " +
                   " AND substring(a.time,1,10) BETWEEN '"+paramsBean.getDataList()[0]+"' AND '"+paramsBean.getDataList()[1]+"' "
                   +"AND substring(substring(time,12,16),1,5) BETWEEN "+ timeSql;
        }else if(paramsBean.getIsContinuous().equals("false")){      //间隔选择时间
            sql2 = "SELECT count(DISTINCT car_id) as count1,count(DISTINCT id) as count2,segment_id FROM trip_segment_new WHERE segment_id in ("+paramsBean.getSegmentId()[0]+") " +
                    " AND substring(time,1,10) in "+getDataSql(paramsBean.getDataList())+"AND substring(substring(time,12,16),1,5) BETWEEN "+ timeSql;
        }
        List<Map<String,Object>> carList = jdbcTemplate.queryForList(sql2);     //有几辆车经过
        map.put("velchels",carList.get(0).get("count1"));

        map.put("times",carList.get(0).get("count2"));

        resultList.add(map);
        return resultList;
    }

    /**
     * 选择多个路段查看信息
     * @param paramsBean
     * @return
     */
    public List<Map<String,Object>> getMultiPointInfo(ParamsBean paramsBean){
        List<Map<String,Object>> resultList = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();

        String sql1 = "SELECT DISTINCT en_name FROM gaosu WHERE id="+ paramsBean.getRoadSecList()[0];
        List<Map<String,Object>> nameMap = jdbcTemplate.queryForList(sql1);
        if(nameMap.size()!=0){
            map.put("road",nameMap.get(0).get("road_name"));           //路名
        }
        String timeSql = " '"+paramsBean.getTimeFrame()[0]+"' AND '"+paramsBean.getTimeFrame()[1]+"'";

        String sql2 = "";
        if(paramsBean.getIsContinuous().equals("true")){      //连续选择时间
            sql2 = "SELECT count(DISTINCT car_id) as count1,count(DISTINCT id) as count2,segment_id FROM trip_segment_new WHERE segment_id in ("+getSegmentSql(paramsBean.getSegmentId())+") " +
                    " AND substring(a.time,1,10) BETWEEN '"+paramsBean.getDataList()[0]+"' AND '"+paramsBean.getDataList()[1]+"' "
                    +"AND substring(substring(time,12,16),1,5) BETWEEN "+ timeSql;
        }else if(paramsBean.getIsContinuous().equals("false")){      //间隔选择时间
            sql2 = "SELECT count(DISTINCT car_id) as count1,count(DISTINCT id) as count2,segment_id FROM trip_segment_new WHERE segment_id in ("+getSegmentSql(paramsBean.getSegmentId())+") " +
                    " AND substring(time,1,10) in "+getDataSql(paramsBean.getDataList())+"AND substring(substring(time,12,16),1,5) BETWEEN "+ timeSql;
        }
        List<Map<String,Object>> carList = jdbcTemplate.queryForList(sql2);     //有几辆车经过
        map.put("velchels",carList.get(0).get("count1"));

        map.put("times",carList.get(0).get("count2"));

        resultList.add(map);
        return resultList;
    }

    //选多条路
    private String getRoadSql(Integer[] roads_id){
        String roadSql = " ";
        for(int i=0; i<roads_id.length; i++){
            roadSql = roadSql + roads_id[i] +",";
        }
        roadSql = "("+ roadSql.substring(0,roadSql.length()-1)+")";
        return roadSql;
    }

    //选多个日期
    private String getDataSql(String[] datas){
        String dataSql = "";
        for(int i=0; i<datas.length; i++){
            dataSql = dataSql +"'"+datas[i]+"'"+",";
        }
        dataSql = "("+ dataSql.substring(0,dataSql.length()-1)+")";
        return dataSql;
    }

    //选多个路段
    private String getSegmentSql(Integer[] segmentId){
        String segmentIdSql = "";
        for(int i=0; i<segmentId.length; i++){
            segmentIdSql = segmentIdSql +segmentId[i].toString()+",";
        }
        segmentIdSql = "("+ segmentIdSql.substring(0,segmentIdSql.length()-1)+")";
        return segmentIdSql;
    }

    //路段信息

}
