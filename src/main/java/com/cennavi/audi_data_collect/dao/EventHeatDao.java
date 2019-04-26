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

    public long getCurrentNum(String time){
        String sql="SELECT count(DISTINCT car_id) FROM audi_poc_car_heat WHERE hour='"+time+"'";
        Map<String,Object> num = jdbcTemplate.queryForMap(sql);
        return (long) num.get("count");
    }


}
