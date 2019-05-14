package com.cennavi.audi_data_collect.dao;

import com.cennavi.audi_data_collect.util.CommUtils;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

import net.sf.json.JSONArray;
import no.ecc.vectortile.VectorTileEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by cennavi on 2019/4/25.
 */
@Component
public class EventHeatDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getGaoSuLines(){
        String sql="select name,st_astext(st_transform(geom,4527)) as wkt,road_id from gaosu where valid=1";
        return jdbcTemplate.queryForList(sql);
    }
    
    public void getDealEventRelationship(){
        String sql="select event_id from collection_info_new ";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        String sql2 = "";
        String sql3 = "";
        Map<String, Object> map;
        for(int i=0;i<list.size();i++) {
        	sql2 = "select * from gaosu_segment order by geom <-> (select geom from collection_info_new where event_id="+list.get(i).get("event_id")+") limit 1;";
        	map = jdbcTemplate.queryForMap(sql2);
        	if(map != null) {
        		sql3 = "update collection_info_new set segment_id="+map.get("id")+" where event_id="+list.get(i).get("event_id");
        		jdbcTemplate.execute(sql3);
        	}
        }
    }
    
    public void insertSegment(Map<String, Object> map) {
    	String sql="insert into gaosu_segment(road_name,road_id,geom) values ('"+map.get("road_name")+"',"+map.get("road_id")+",st_transform(st_geomfromtext('"+map.get("geom")+"',4527),4326))";
        jdbcTemplate.execute(sql);
    }
    
    public Map<String, Object> eventAggregateFigure(Map<String, Object> map) throws Exception {
    	String isContinuous = map.get("isContinuous").toString();
    	String sql = "";
    	
    	sql = "select c.event_id eventid,e.type_code typeId,e.event_name_en typeName,c.geom,st_astext(c.geom) as wkt from collection_info_new c " + 
				"left join gaosu_segment s on s.id=c.segment_id  " + 
    			"left join event_type e on c.event_type=e.type_code " + 
				"where 1=1 ";
		if(map.get("city") != null) {
			sql += " and c.city_name='"+map.get("city")+"'";
		}
		if(map.get("dateList") != null) {
			JSONArray dateList = (JSONArray)map.get("dateList");
			if(isContinuous.equals("true")) {
				if(dateList.size()>0)sql += " and substring(c.upload_time,0,11) between '"+dateList.get(0)+"' and '"+dateList.get(1)+"'";
	    	}else {
	    		String es2 = "(";
				for(int i=0;i<dateList.size();i++) {
					es2 += "'" + dateList.get(i) + "',";
				}
				es2 = es2.substring(0, es2.length()-1) + ")";
				if(dateList.size()>0)sql += " and substring(c.upload_time,0,11) in "+es2;
	    	}
		}
		if(map.get("eventsList") != null) {
			JSONArray eventsList = (JSONArray)map.get("eventsList");
			String es = "(";
			for(int i=0;i<eventsList.size();i++) {
				if(!eventsList.get(i).toString().equals("01")) {
					es += "'" + eventsList.get(i) + "',";
				}else {
					es += "'111',";
				}
			}
			es = es.substring(0, es.length()-1) + ")";
			if(eventsList.size()>0 && es.length()>2)sql += " and c.event_type in " + es;
		}
		if(map.get("timeFrame") != null) {
			JSONArray timeFrame = (JSONArray)map.get("timeFrame");
			if(timeFrame.size()>0)sql += " and substring(c.upload_time,12,5) between '"+timeFrame.get(0)+"' and '"+timeFrame.get(1)+"'";
		}
		if(map.get("roadSecList") != null) {
			JSONArray roadSecList = (JSONArray)map.get("roadSecList");
			String es1 = "(";
			for(int i=0;i<roadSecList.size();i++) {
				es1 += roadSecList.get(i) + ",";
			}
			es1 = es1.substring(0, es1.length()-1) + ")";
			if(roadSecList.size()>0)sql += " and s.r_id in "+es1;
		}
		System.out.println(sql);
		Map<String, Object> geojson = CommUtils.getGeojson(jdbcTemplate, sql);
		return geojson;
    }
    
    public Map<String, Object> getEventInfo(Integer eventId) {
    	String sql="select c.event_id,e.event_name_en,replace(replace(c.upload_time,'-','/'),':','/'),s.en_name from collection_info_new c " + 
    			"left join gaosu_segment g on g.id=c.segment_id " + 
    			"left join gaosu s on g.road_id=s.road_id " + 
    			"left join event_type e on e.type_code=c.event_type " + 
    			"where c.event_id="+eventId;
    	return jdbcTemplate.queryForMap(sql);
    }
    
    public List<Map<String, Object>> getRoadList() throws Exception{
    	String sql = "SELECT g.en_name as name,g.r_id FROM gaosu g where g.r_id>0 group by g.en_name,g.r_id order by g.r_id";
    	return jdbcTemplate.queryForList(sql);
    }
    

}
