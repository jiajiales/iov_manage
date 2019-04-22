package com.cennavi.audi_data_collect.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cennavi.audi_data_collect.service.CarHeatService;

/**
 * Created by cennavi on 2019/3/12.
 */
@RestController
@RequestMapping("/carHeat")
public class CarHeatController {
    @Autowired
    private CarHeatService carHeatService;

    /**
     * 得到当前车辆数.
     */
    @ResponseBody
    @RequestMapping("/getCurrentNum")
    public long getCurrentNum(@RequestParam("time") String time){
        try {
            long num = carHeatService.getCurrentNum(time);
            return num;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * 北京车辆数top5 的区.
     */
    @ResponseBody
    @RequestMapping("/getTop5")
    public List<Map<String,Object>> getTop5(String time){
        try {
            List<Map<String,Object>> list = carHeatService.getTop5(time);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    /**
     * 自动播放热力.
     */
    @RequestMapping(value = "/getCarLiveJobData/{z}/{x}/{y}/{time}", produces = "application/x-protobuf")
    public String getCarHeatMap(@PathVariable("z") int z , @PathVariable("x") int x , @PathVariable("y") int y ,
                                @PathVariable("date") String time ,HttpServletResponse response){

        try {
            byte[] data =carHeatService.getCarHeatMap(x,y,z,time);
            if (data != null && data.length > 0){
                response.getOutputStream().write(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
