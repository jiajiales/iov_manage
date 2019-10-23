//package com.cennavi.vehicle_networking_data.utils;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.google.common.util.concurrent.ThreadFactoryBuilder;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.tomcat.util.http.fileupload.IOUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.Base64;
//import java.util.List;
//import java.util.concurrent.*;
//import java.util.zip.GZIPInputStream;
//
///**
// * Created by 60195 on 2019/10/15.
// */
//public class MatchTrackingUtilNew {
//
//    @Value("${params.trackingUrl}")
//    static String  subpointsUrl = "http://117.48.214.8:9089/TrafficSS/Service/getSubpointss";
//    static String  trackingUrl = "http://117.48.214.8:9089/TrafficSS/Service/getHttpMultiTrajectorys";
//    private static Logger log = LoggerFactory.getLogger(MatchTrackingNewUtil.class);
//
////	private static String trackingUrl = "http://117.51.157.134:9880/TrafficSS/Service/getHttpMultiTrajectorys";
//
//     static   ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(false).setNameFormat("commuteTimeByCarTypeThread--%d").build();
//     static ExecutorService singleThreadPool = new ThreadPoolExecutor(5, 5,
//            0L, TimeUnit.MILLISECONDS,
//            new LinkedBlockingQueue<Runnable>(1024), threadFactory, new ThreadPoolExecutor.AbortPolicy());
//
//    public static JSONArray trackingMatch(List<String> listPoint, List<Long> listTime) {
//        JSONArray arrTrack = null;
//        if (listPoint == null || listPoint.size() == 0 || listTime == null || listTime.size() == 0) {
//            return arrTrack;
//        }
//        //构造json格式的参数
//        StringBuffer sb = new StringBuffer();
//        sb.append("{");
//        sb.append("\"trajCount\":\"63\"");
//        sb.append(",");
//        sb.append("\"subParam\":");
//        sb.append("[");
//        for (int i = 0; i < listPoint.size(); i++) {
//            long gpstime = listTime.get(i);
//            String spoint = listPoint.get(i);
//            String[] points = spoint.split(" ");
//            sb.append("{");
//            sb.append("\"deviceID\":\"cennavi_location_2\"");
//            sb.append(",");
//            sb.append("\"gpstime\":" + gpstime / 1000);
//            sb.append(",");
//            sb.append("\"longitude\":" + (int)(Double.parseDouble(points[0]) * 10000000));
//            sb.append(",");
//            sb.append("\"latitude\":" + (int)(Double.parseDouble(points[1]) * 10000000));
//            sb.append("}");
//            if (i < listPoint.size() - 1) {
//                sb.append(",");
//            }
//        }
//        sb.append("]");
//        sb.append("}");
//
//         String shapesText;
//         String trajectoryResult;
//
//        shapesText = HttpRequestUtil.sendPost(subpointsUrl, sb.toString());
//
//       trajectoryResult = HttpRequestUtil.sendPost(trackingUrl, sb.toString());
//        //投影点接口返回道路的形状点
//        JSONObject resultJson = JSONObject.parseObject(shapesText);
//        Object state = resultJson.get("header");
//        String text1 = "";
//        if (state != null && state.toString().equals("CODE_200")) {
//            Object data = resultJson.get("responseBody");
//            text1 = new String(uncompress(Base64.getDecoder().decode(data.toString().getBytes())));
//        }
//      //投影点接口返回道路的形状点
//        JSONObject trajectoryJson = JSONObject.parseObject(trajectoryResult);
//        Object state2 = trajectoryJson.get("header");
//        String text2 = "";
//        if (state != null && state.toString().equals("CODE_200")) {
//            Object data2 = trajectoryJson.get("responseBody");
//            text2 = new String(uncompress(Base64.getDecoder().decode(data2.toString().getBytes())));
//        }
//        JSONArray data2 = JSONArray.parseArray(text2);
//        JSONArray speedArr1 = null;
//        if(data2!= null && data2.size() > 0){
//            JSONObject obj = JSONObject.parseObject(data2.get(0).toString());
////            System.out.println(obj.toJSONString());
//            speedArr1 = obj.getJSONArray("trajectory");
//        }
//        //log.info("match-response: " + text);
//        if (StringUtils.isEmpty(text1)) {
//            return arrTrack;
//        }
//        JSONArray data = JSONArray.parseArray(text1);
//        if (data != null && data.size() > 0) {
//
//            JSONObject obj = JSONObject.parseObject(data.get(0).toString());
////            System.out.println(obj.toJSONString());
//            JSONArray arr1 = obj.getJSONArray("trajectory");
//            if (arr1 != null && arr1.size() > 0) {
//                arrTrack = new JSONArray();
//                for (int i = 0; i < arr1.size(); i++) {
//
//                    JSONObject obj2 = (JSONObject) arr1.get(i);
//
//                    if (obj2 != null) {
////                        int avgSpeed = obj2.getIntValue("avgSpeed");
//                        String shapes = obj2.getString("shapes");
//                        if (!StringUtils.isEmpty(shapes)) {
//                            String[] shape = shapes.split(";");
//
//                            //1 - 判断第一个link到达时,只截取到距离第一个点最近的link上的点
//
//                            //2 - 判断最后一个link到达时,只截取到距离最后一个点最近的link上的点
//                            if (i == arr1.size() - 1) {
//
//                            } else //3 - 中间link上的点全部加到数组中
//                            {
//                                for (int j2 = 1; j2 < shape.length; j2++) {
//                                    int avgSpeed = 30;
//                                    for(int j=0; j<speedArr1.size(); j++){
//                                        JSONObject speedJson = (JSONObject) speedArr1.get(j);
//                                        if(obj2.getString("linkId").equals(speedJson.getString("linkID"))){
//                                            avgSpeed = speedJson.getIntValue("avgSpeed");
//                                            break;
//                                        }
//                                    }
//                                    String slonlat = shape[j2];
//                                    String[] lonlat = null;
//                                    lonlat = slonlat.split(" ");
//                                    if(lonlat[0].contains(",")){
//                                        lonlat = lonlat[0].replace(","," ").split(" ");
//                                    }
//                                    JSONArray arrTmp = new JSONArray();
//                                    double lon = Double.parseDouble(lonlat[0]);
//                                    double lat = Double.parseDouble(lonlat[1]);
//                                    arrTmp.add(lon);//lon
//                                    arrTmp.add(lat);//lat
//                                    arrTmp.add(avgSpeed);//speed
//                                    //添加坐标数组
//                                    arrTrack.add(arrTmp);
//
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        return arrTrack;
//    }
//
//    /*
//    * 解压缩数据 (Snappy)
//    */
//    public static   byte[] uncompress(byte[] arg0) {
//        ByteArrayOutputStream output = new ByteArrayOutputStream();
//        GZIPInputStream input = null;
//        try {
//            input = new GZIPInputStream(new ByteArrayInputStream(arg0));
//            IOUtils.copyLarge(input, output);
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } finally {
//            IOUtils.closeQuietly(input);
//            IOUtils.closeQuietly(output);
//        }
//        return output.toByteArray();
//    }
//}
