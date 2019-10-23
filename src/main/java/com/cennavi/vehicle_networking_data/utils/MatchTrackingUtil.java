package com.cennavi.vehicle_networking_data.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MatchTrackingUtil {

	private static Logger log = LoggerFactory.getLogger(MatchTrackingUtil.class);
	
//	private static String url = "http://ms.minedata.cn/service/lbs/service/search/multi-trajectory?appKey=79a7d9ef4ab5494faa5d80ae62e58f16";       //申请的
	private static String url = "http://mineservice.minedata.cn/service/lbs/service/search/multi-trajectory?appKey=3a127c8980e445a08013b08c33ef37a6";       //上海用的
//	private static String url = "http:// 117.51.157.134:9880/TrafficStandaloneServer/getHttpMultiTrajectorys";     //部署的
	
	public static JSONArray trackingMatch(List<String> listPoint, List<Long> listTime) {
		JSONArray arrTrack = null;
		if(listPoint==null || listPoint.size()==0 || listTime==null || listTime.size()==0) {
			return arrTrack;
		}
		//构造json格式的参数
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for(int i=0; i<listPoint.size(); i++) {
			long gpstime = listTime.get(i);
			String spoint = listPoint.get(i);
			String[] points = spoint.split(" ");
			sb.append("{");
			sb.append("\"deviceID\":\"cennavi_location_2\"");
			sb.append(",");
			sb.append("\"gpstime\":" + gpstime / 1000);
			sb.append(",");
			sb.append("\"longitude\":" + Double.parseDouble(points[0]) * 10000000);
			sb.append(",");
			sb.append("\"latitude\":" + Double.parseDouble(points[1]) * 10000000);
			sb.append("}");
			if(i < listPoint.size()-1) {
				sb.append(",");
			}
		}
		sb.append("]");
		//log.info("match-params: " + sb.toString());
		String text = HttpRequestUtil.sendPost(url, sb.toString());
		//log.info("match-response: " + text);
		if(StringUtils.isEmpty(text)) {
			return arrTrack;
		}
		
        JSONObject obj = JSONObject.parseObject(text);
        int errcode = obj.getIntValue("errcode");
        if(errcode == 0) {
        	JSONArray arr = obj.getJSONArray("data");
        	if(arr!=null && arr.size() > 0) {
        		JSONObject obj1 = (JSONObject)arr.get(0);
        		if(obj1 != null) {
        			JSONArray arr1 = obj1.getJSONArray("trajectory");
        			if(arr1!=null && arr1.size() > 0) {
        				arrTrack = new JSONArray();

        				for(int i=0; i<arr1.size(); i++) {

        					JSONObject obj2 = (JSONObject)arr1.get(i);

        					if(obj2 != null) {
            					int avgSpeed = obj2.getIntValue("avgSpeed");
            					String shapes = obj2.getString("shapes");
            					if(!StringUtils.isEmpty(shapes)) {
            						String[] shape = shapes.split(";");

            						//1 - 判断第一个link到达时,只截取到距离第一个点最近的link上的点
            						if(i == 0) {
                						/*double minDis = 0;
                						int minIdx = 0;
                						String sFirstPoint = listPoint.get(0);
                						String[] firstLonLat = sFirstPoint.split(" ");
                						double llon = Double.parseDouble(firstLonLat[0]);
            							double llat = Double.parseDouble(firstLonLat[1]);
                						for(int j01=1; j01<shape.length; j01++) {
	            							String slonlat = shape[j01];
	            							String[] lonlat = null;
	            							lonlat = slonlat.split(" ");
	            							double lon = Double.parseDouble(lonlat[0]);
	            							double lat = Double.parseDouble(lonlat[1]);
	            							double distance = AdminUtil.distance(lat, lon, llat, llon);
	            							if(j01 == 1) {
	            								minDis = distance;
	            								minIdx = 1;
	            							}else {
	            								minIdx = distance < minDis ? j01 : minIdx;
	            							}
	            						}
                						//把有效的轨迹点添加到数组
                						for(int j02=minIdx; j02<shape.length; j02++) {
                							String slonlat = shape[j02];
	            							String[] lonlat = null;
	            							lonlat = slonlat.split(" ");
	            							JSONArray arrTmp = new JSONArray();
	            							double lon = Double.parseDouble(lonlat[0]);
	            							double lat = Double.parseDouble(lonlat[1]);
                							arrTmp.add(lon);//lon
	            							arrTmp.add(lat);//lat
	            							arrTmp.add(avgSpeed);//speed
	            							//添加坐标数组
	            							arrTrack.add(arrTmp);
                						}*/
            							JSONArray arrTmp = new JSONArray();
            							String sFirstPoint = listPoint.get(0);
                						String[] firstLonLat = sFirstPoint.split(" ");
                						double flon = Double.parseDouble(firstLonLat[0]);
            							double flat = Double.parseDouble(firstLonLat[1]);
            							arrTmp.add(flon);//lon
            							arrTmp.add(flat);//lat
            							arrTmp.add(50.0);//speed
                					}
            						//2 - 判断最后一个link到达时,只截取到距离最后一个点最近的link上的点
                					if(i == arr1.size()-1) {
                						/*double minDis = 0;
                						int minIdx = 0;
                						String sLastPoint = listPoint.get(listPoint.size() - 1);
                						String[] lastLonLat = sLastPoint.split(" ");
                						double llon = Double.parseDouble(lastLonLat[0]);
            							double llat = Double.parseDouble(lastLonLat[1]);
                						for(int j11=1; j11<shape.length; j11++) {
	            							String slonlat = shape[j11];
	            							String[] lonlat = null;
	            							lonlat = slonlat.split(" ");
	            							double lon = Double.parseDouble(lonlat[0]);
	            							double lat = Double.parseDouble(lonlat[1]);
	            							double distance = AdminUtil.distance(lat, lon, llat, llon);
	            							if(j11 == 1) {
	            								minDis = distance;
	            								minIdx = 1;
	            							}else {
	            								minIdx = distance < minDis ? j11 : minIdx;
	            							}
	            						}
                						//把有效的轨迹点添加到数组
                						for(int j12=1; j12<=minIdx; j12++) {
                							String slonlat = shape[j12];
	            							String[] lonlat = null;
	            							lonlat = slonlat.split(" ");
	            							JSONArray arrTmp = new JSONArray();
	            							double lon = Double.parseDouble(lonlat[0]);
	            							double lat = Double.parseDouble(lonlat[1]);
                							arrTmp.add(lon);//lon
	            							arrTmp.add(lat);//lat
	            							arrTmp.add(avgSpeed);//speed
	            							//添加坐标数组
	            							arrTrack.add(arrTmp);
                						}*/
                						JSONArray arrTmp = new JSONArray();
                						String sLastPoint = listPoint.get(listPoint.size() - 1);
                						String[] lastLonLat = sLastPoint.split(" ");
                						double llon = Double.parseDouble(lastLonLat[0]);
            							double llat = Double.parseDouble(lastLonLat[1]);
            							arrTmp.add(llon);//lon
            							arrTmp.add(llat);//lat
            							arrTmp.add(50.0);//speed
                					}
                					else //3 - 中间link上的点全部加到数组中
                					{
	            						for(int j2=1; j2<shape.length; j2++) {
	            							String slonlat = shape[j2];
	            							String[] lonlat = null;
	            							lonlat = slonlat.split(" ");
	            							JSONArray arrTmp = new JSONArray();
	            							double lon = Double.parseDouble(lonlat[0]);
	            							double lat = Double.parseDouble(lonlat[1]);
	            							arrTmp.add(lon);//lon
	            							arrTmp.add(lat);//lat
	            							arrTmp.add(avgSpeed);//speed
	            							//添加坐标数组
	            							arrTrack.add(arrTmp);
	            							
	            						}
                					}
            					}
            				}
        				}
        			}
        		}
        	}
        }

        return arrTrack;
	}
	
}
