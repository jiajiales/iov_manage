package com.cennavi.audi_data_collect.dao;

import com.cennavi.audi_data_collect.util.CommUtils;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
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
    	String sql = "select * from guangdong_traffic_info ";
		Map<String, Object> geojson = CommUtils.getGeojson(jdbcTemplate, sql);
		return geojson;
    }
    

}
