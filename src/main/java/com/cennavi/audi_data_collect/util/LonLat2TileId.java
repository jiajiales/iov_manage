package com.cennavi.audi_data_collect.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cennavi on 2019/3/20.
 */

//zoom=15时，Google tile标准下将经纬度对应到瓦片标号下

public class LonLat2TileId {

    public static Map<String,Integer> lonLat2tileId(double lon, double lat, int zoom){
        Map<String,Integer> tileMap = new HashMap<>();
         int  tile_x = lon2tile(lon,zoom);
        int tile_y = lat2tile(lat,zoom);
        tileMap.put("x",tile_x);
        tileMap.put("y",tile_y);
        return tileMap;
    }

    private static int lon2tile(double lon, int zoom){

        return (int) Math.floor(((lon+180)/ 360)*(Math.pow(2,zoom)));
    }

    private static int lat2tile(double lat, int zoom){

        return (int) Math.floor((1-Math.log(Math.tan(lat * Math.PI/180) + 1/Math.cos(lat*Math.PI/180))/Math.PI)/2 * Math.pow(2,zoom));

    }
}
