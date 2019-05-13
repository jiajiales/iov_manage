package com.cennavi.audi_data_collect.controller;


import com.alibaba.fastjson.JSONObject;
import com.cennavi.audi_data_collect.bean.ParamsBean;
import com.cennavi.audi_data_collect.service.HomeService;
import com.cennavi.audi_data_collect.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cennavi on 2019/4/29.
 */
@RestController
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private HomeService homeService;

    @ResponseBody
    @RequestMapping("/getRoad")
    public List<Map<String,Object>> getRoad(){
        Map<String,Object> state = new HashMap<>();
        try {
            List<Map<String,Object>> list = homeService.getRoad();

            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回全部路段的id
     */
    @RequestMapping(value = "/getSegmentId/{x}/{y}/{z}", produces = "application/x-protobuf")
    public String getSegmentId(@PathVariable("x") Integer x,@PathVariable("y") Integer y,@PathVariable("z") Integer z,Integer segment1,Integer segment2, HttpServletResponse response){
        try{
            byte[] result = homeService.getSegmentId(x,y,z);
            if(result != null && result.length > 0){
               response.getOutputStream().write(result);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 行人事件热力图
     */
    /*@RequestMapping(value = "/getVRUEvent/{x}/{y}/{z}/{params}", produces = "application/x-protobuf")
    public String getVRUEvent(@PathVariable("x") int x , @PathVariable("y") int y , @PathVariable("z") int z ,
                              @RequestBody ParamsBean paramsBean, HttpServletResponse response){
        try{
            String event = "";
            for(int i=0; i<paramsBean.getEventsList().length; i++){
                event = event + paramsBean.getEventsList()[i].toString()+",";
            }
            if(event.contains("01")){
            }else {
                return null;
            }
            byte[] result = homeService.getVRUEvent(x,y,z,paramsBean);
            if(result != null && result.length > 0){
                response.getOutputStream().write(result);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/
    @RequestMapping(value = "/getVRUEvent/{x}/{y}/{z}", produces = "application/x-protobuf")
    public String getVRUEvent(@PathVariable("x") int x , @PathVariable("y") int y , @PathVariable("z") int z ,
                              String city,String eventsList,String dataList,String dataListFormat,String roadSecList,
                              String timeFrame,String isContinuous,  HttpServletResponse response){

        try{

           // ParamsBean paramsBean = (ParamsBean) JsonUtil.toObject(paramBody,ParamsBean.class);
            String event="";
            if(eventsList==null){
                return null;
            }
            if(eventsList.contains("01")){
            }else {
                return null;
            }
            byte[] result = homeService.getVRUEvent(x,y,z,city,eventsList,dataList,dataListFormat,roadSecList,timeFrame,isContinuous);
            if(result != null && result.length > 0){
                response.getOutputStream().write(result);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 选择某一个路段查看信息
     */
    @ResponseBody
    @RequestMapping("/getPointInfo")
//    public List<Map<String,Object>> getPointInfo(Integer id,String[] dataList,String[] timeFrame,String isContinuous){
    public List<Map<String,Object>> getPointInfo(@RequestBody ParamsBean paramsBean){
        Map<String,Object> state = new HashMap<>();
        //Map<String,Object> info = new HashMap<>();
        try {
            List<Map<String,Object>> list = homeService.getPointInfo(paramsBean);

            state.put("state",200);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        state.put("state",500);
        return null;
    }

    /**
     * 选择多个路段查看信息
     */
    @ResponseBody
    @RequestMapping("/getMutiPointInfo")
//    public List<Map<String,Object>> getPointInfo(Integer id,String[] dataList,String[] timeFrame,String isContinuous){
    public Object getMutiPointInfo(@RequestBody ParamsBean paramsBean){
        Map<String,Object> state = new HashMap<>();
       if(paramsBean.getRoadSecList().length>1){
           state.put("desc","请选择同一条道路!");
           return state;
       }
        try {
            Map<String,Object> map1 = homeService.getMultiPointInfo(paramsBean);
            return map1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 车辆经过次数热力图
     * @param paramsBean
     * @return
     */
    @ResponseBody
    @RequestMapping("/getTimesHeat")
    public Map<String,Object> getTimesHeat(@RequestBody ParamsBean paramsBean){
        try{
            if(paramsBean.getRoadSecList()==null || paramsBean.getRoadSecList().length==0){
                return null;
            }
            Map<String,Object> result = homeService.getTimesHeat(paramsBean);
            return result;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
