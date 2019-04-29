package com.cennavi.audi_data_collect.dao;

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
    
    public void insertSegment(Map<String, Object> map) {
    	String sql="insert into gaosu_segment(road_name,road_id,geom) values ('"+map.get("road_name")+"',"+map.get("road_id")+",st_transform(st_geomfromtext('"+map.get("geom")+"',4527),4326))";
        jdbcTemplate.execute(sql);
    }

}
