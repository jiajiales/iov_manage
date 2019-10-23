package com.cennavi.vehicle_networking_data.controller;

import com.alibaba.fastjson.JSONObject;
import com.cennavi.vehicle_networking_data.service.HomeService;
import com.cennavi.vehicle_networking_data.utils.CoordinateTransformUtils;
import com.cennavi.vehicle_networking_data.utils.CoordinateTransformUtils2;
import com.cennavi.vehicle_networking_data.utils.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * Created by 60195 on 2019/9/18.
 */
@RestController
@RequestMapping(value = "/home")
public class HomeController {

    @Autowired
    private HomeService homeService;

    /**
     * 全部车辆位置散点图
     * @param z zoom级别
     * @param x  瓦片x
     * @param y  瓦片y
     * @return
     */
    @RequestMapping(value = { "/realtimeScatter/{z}/{x}/{y}" }, produces="application/x-protobuf")
    public ResponseEntity<byte[]> realtimeScatter(@PathVariable("z") int z , @PathVariable("x") int x , @PathVariable("y") int y){
        byte[] data = homeService.realtimeScatter(x, y, z);
        return ResponseEntity.ok(data);
    }

    /**
     * 根据条件获取车辆位置散点图
     * @param z zoom级别
     * @param x  瓦片x
     * @param y  瓦片y
     * @return
     */
    @RequestMapping(value = { "/realtimeScatterByCondition/{z}/{x}/{y}/{business}/{group}" }, produces="application/x-protobuf")
    public ResponseEntity<byte[]> realtimeScatterByCondition(@PathVariable("z") int z , @PathVariable("x") int x , @PathVariable("y") int y,@PathVariable("business") String business,@PathVariable("group") String group ){
        byte[] data = homeService.realtimeScatterByCondition(x, y, z,business,group);
        return ResponseEntity.ok(data);
    }

    /**
     * 全部车辆位置热力图
     * @param z
     * @param x
     * @param y
     * @return
     */
    @RequestMapping(value = { "/heatMap/{z}/{x}/{y}" }, produces="application/x-protobuf")
    public String heatMap(@PathVariable("z") int z , @PathVariable("x") int x , @PathVariable("y") int y,HttpServletResponse response){
        byte[] data = homeService.heatMap(x, y, z);
        try {
            response.getOutputStream().write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 车辆实时位置
     * @param cph 车牌号
     * @param time 时间
     * @param type 所属类型  1：执法，2：绿化，3：环卫，0所有
     * @return
     */
    @RequestMapping(value = "/realtimeTracking")
    public ResponseEntity<JSONObject> realtimeTracking(String cph, String time,Integer type){
        JSONObject data = homeService.realtimeTracking(cph,time,type);
        return ResponseEntity.ok(data);
    }

    /**
     * 根据条件模糊查询车牌号
     * @param business
     * @Param group
     * @return List<Map<String,Object>>
     */
    @RequestMapping(value = "/fuzzyMatch")
    public List<Map<String,Object>> fuzzyMatch(String business, String group){
        String sql = "";

        return null;
    }

    /**
     * 当前跟踪车辆详细信息
     * @param cph  车牌号
     * @param date  日期
     * @param type  所属类型  1：执法，2：绿化，3：环卫，0所有
     * @return
     */
    @RequestMapping("/carMsg")
    public Map<String,Object> getCarMsg(String cph,String date,Integer type){
        try {
            return  homeService.getCarMsg(cph,date,type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @RequestMapping("/carSimpleInfo")
    public Map<String,Object> carSimpleInfo(String cph){
        try {
            return  homeService.carSimpleInfo(cph);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/02To84")
    public void to84(double lon, double lat){
        Point point = CoordinateTransformUtils2.gcj02ToWgs84(lon,lat);
        System.out.println(point.getLng()+","+ point.getLat());

    }

}
