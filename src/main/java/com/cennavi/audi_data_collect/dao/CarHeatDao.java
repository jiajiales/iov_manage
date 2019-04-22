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
 * Created by cennavi on 2019/3/12.
 */
@Component
public class CarHeatDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public long getCurrentNum(String time){
        String sql="SELECT count(DISTINCT car_id) FROM audi_poc_car_heat WHERE hour='"+time+"'";
        Map<String,Object> num = jdbcTemplate.queryForMap(sql);
        return (long) num.get("count");
    }

    public List<Map<String,Object>> getTop5(String time){
        String sql = "SELECT count(*) as count,a.name_py FROM audi_poc_beijing_district as a, audi_poc_car_heat as b" +
                " WHERE st_intersects(a.geom,b.geom) AND  b.hour='"+time+"' GROUP BY a.name_py " +
                " ORDER BY count desc LIMIT 5;";
        List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
        return  list;
    }

    public byte[] getCarHeatMap(int z,int x,int y, String time) throws Exception{

        String sql = "SELECT st_astext(geom)as geom ,hour FROM audi_poc_car_heat --WHERE hour='"+time+"'";

        String tile = "";//TileUtils.parseXyz2Bound(x, y, z);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,tile);

        VectorTileEncoder vte = new VectorTileEncoder(4096, 16, false);

        for (Map<String, Object> m : list) {
            String wkt = (String) m.get("geom");

            Geometry geom = new WKTReader().read(wkt);

            //TileUtils.convert2Piexl(x, y, z, geom);

            vte.addFeature("gps", m, geom);
        }
        return vte.encode();
    }



}
