package com.cennavi.vehicle_networking_data.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPInputStream;

@Component
public class MatchTrackingNewUtil {

	@Value("${params.trackingUrl}")
	static String  subpointsUrl = "http://117.48.214.8:9089/TrafficSS/Service/getSubpointss";
	static String  trackingUrl = "http://117.48.214.8:9089/TrafficSS/Service/getHttpMultiTrajectorys";
	private static Logger log = LoggerFactory.getLogger(MatchTrackingNewUtil.class);
	
//	private static String trackingUrl = "http://117.51.157.134:9880/TrafficSS/Service/getHttpMultiTrajectorys";
	
	public static JSONArray trackingMatch(List<String> listPoint, List<Long> listTime) {
		JSONArray arrTrack = null;
		if (listPoint == null || listPoint.size() == 0 || listTime == null || listTime.size() == 0) {
			return arrTrack;
		}
		//构造json格式的参数
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"trajCount\":\"63\"");
		sb.append(",");
		sb.append("\"subParam\":");
		sb.append("[");
		for (int i = 0; i < listPoint.size(); i++) {
			long gpstime = listTime.get(i);
			String spoint = listPoint.get(i);
			String[] points = spoint.split(" ");
			sb.append("{");
			sb.append("\"deviceID\":\"cennavi_location_2\"");
			sb.append(",");
			sb.append("\"gpstime\":" + gpstime / 1000);
			sb.append(",");
			sb.append("\"longitude\":" + (int)(Double.parseDouble(points[0]) * 10000000));
			sb.append(",");
			sb.append("\"latitude\":" + (int)(Double.parseDouble(points[1]) * 10000000));
			sb.append("}");
			if (i < listPoint.size() - 1) {
				sb.append(",");
			}
		}
		sb.append("]");
		sb.append("}");

		String result = HttpRequestUtil.sendPost(trackingUrl, sb.toString());
		JSONObject resultJson = JSONObject.parseObject(result);
		Object state = resultJson.get("header");
		String text = "";
		if (state != null && state.toString().equals("CODE_200")) {
			Object data = resultJson.get("responseBody");
			text = new String(uncompress(Base64.getDecoder().decode(data.toString().getBytes())));
		}

		//log.info("match-response: " + text);
		if (StringUtils.isEmpty(text)) {
			return arrTrack;
		}
		JSONArray data = JSONArray.parseArray(text);
		if (data != null && data.size() > 0) {

		JSONObject obj = JSONObject.parseObject(data.get(0).toString());
		System.out.println(obj.toJSONString());
		JSONArray arr1 = obj.getJSONArray("trajectory");
		if (arr1 != null && arr1.size() > 0) {
			arrTrack = new JSONArray();
			for (int i = 0; i < arr1.size(); i++) {

				JSONObject obj2 = (JSONObject) arr1.get(i);

				if (obj2 != null) {
					int avgSpeed = obj2.getIntValue("avgSpeed");
					String shapes = obj2.getString("shapes");
					if (!StringUtils.isEmpty(shapes)) {
						String[] shape = shapes.split(";");

						//1 - 判断第一个link到达时,只截取到距离第一个点最近的link上的点
						if (i == 0) {

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
						if (i == arr1.size() - 1) {

							JSONArray arrTmp = new JSONArray();
							String sLastPoint = listPoint.get(listPoint.size() - 1);
							String[] lastLonLat = sLastPoint.split(" ");
							double llon = Double.parseDouble(lastLonLat[0]);
							double llat = Double.parseDouble(lastLonLat[1]);
							arrTmp.add(llon);//lon
							arrTmp.add(llat);//lat
							arrTmp.add(50.0);//speed
						} else //3 - 中间link上的点全部加到数组中
						{
							for (int j2 = 1; j2 < shape.length; j2++) {
								String slonlat = shape[j2];
								String[] lonlat = null;
								lonlat = slonlat.split(" ");
								if(lonlat[0].contains(",")){
									lonlat = lonlat[0].replace(","," ").split(" ");
								}
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
        return arrTrack;
	}

	public static JSONArray trackingMatchHistoryCustomer(List<Long> listTime, List<Integer> listSpeed, List<String> listPoint,List<Short> listHeading,String cph) {
		JSONArray result = null;
		if (listPoint == null || listPoint.size() == 0 || listTime == null || listTime.size() == 0) {
			return result;
		}
		//构造json格式的参数
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"trajCount\":\"63\"");
		sb.append(",");
		sb.append("\"subParam\":");
		sb.append("[");
		for (int i = 0; i < listPoint.size(); i++) {
			long gpstime = listTime.get(i);
			String spoint = listPoint.get(i);
			String[] points = spoint.split(" ");
			sb.append("{");
			sb.append("\"deviceID\":\""+cph+"\"");
			sb.append(",");
			sb.append("\"gpstime\":" + gpstime / 1000);
			sb.append(",");

			if(listSpeed.get(i) != 0){
				sb.append("\"speed\":"+ listSpeed.get(i));
				sb.append(",");
			}
			if(listHeading.get(i) != 0){
				sb.append("\"heading\":"+ listHeading.get(i));
				sb.append(",");
			}
			sb.append("\"longitude\":" + (int)(Double.parseDouble(points[0]) * 10000000));
			sb.append(",");
			sb.append("\"latitude\":" + (int)(Double.parseDouble(points[1]) * 10000000));
			sb.append("}");
			if (i < listPoint.size() - 1) {
				sb.append(",");
			}
		}
		sb.append("]");
		sb.append("}");

		String shapesText = HttpRequestUtil.sendPost(trackingUrl, sb.toString());
		//推测接口返回道路的结果
		JSONObject resultJson = JSONObject.parseObject(shapesText);
		Object state = resultJson.get("header");
		String text1 = "";
		if (state != null && state.toString().equals("CODE_200")) {
			Object data = resultJson.get("responseBody");
			text1 = new String(uncompress(Base64.getDecoder().decode(data.toString().getBytes())));
		}
		if (StringUtils.isEmpty(text1)) {
			return result;
		}
		JSONArray data = JSONArray.parseArray(text1);

		JSONArray tracksArr = new JSONArray();
		if (data != null && data.size() > 0) {

			JSONObject obj = JSONObject.parseObject(data.get(0).toString());
			System.out.println(obj.toJSONString());
			JSONArray arr1 = obj.getJSONArray("trajectory");
			if (arr1 != null && arr1.size() > 0) {
				for (int i = 0; i < arr1.size(); i++) {

					JSONObject obj2 = (JSONObject) arr1.get(i);

					if (obj2 != null) {

						String shapes = obj2.getString("shapes");
						if (!StringUtils.isEmpty(shapes)) {
							String[] shape = shapes.split(";");

							//2 - 判断最后一个link到达时,只截取到距离最后一个点最近的link上的点
							if (i == arr1.size() - 1) {

							} else //3 - 中间link上的点全部加到数组中
							{
								for (int j2 = 1; j2 < shape.length; j2++) {
									String slonlat = shape[j2];
									String[] lonlat = null;
									lonlat = slonlat.split(" ");
									if(lonlat[0].contains(",")){
										lonlat = lonlat[0].replace(","," ").split(" ");
									}
									JSONArray arrTmp = new JSONArray();
									double lon = Double.parseDouble(lonlat[0]);
									double lat = Double.parseDouble(lonlat[1]);
									arrTmp.add(lon);//lon
									arrTmp.add(lat);//lat
									tracksArr.add(arrTmp);
								}
							}
						}
					}
				}
			}
		}
		return tracksArr;
	}

	public static JSONObject trackingMatchHistoryOwn(List<Long> listTime, List<Integer> listSpeed, List<String> listPoint,List<Short> listHeading,String cph) {
		JSONObject result = new JSONObject();
		JSONArray arrTrack = new JSONArray();
		if (listPoint == null || listPoint.size() == 0 || listTime == null || listTime.size() == 0) {
			return result;
		}
		//构造json格式的参数
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"trajCount\":\"63\"");
		sb.append(",");
		sb.append("\"subParam\":");
		sb.append("[");
		for (int i = 0; i < listPoint.size(); i++) {
			long gpstime = listTime.get(i);
			String spoint = listPoint.get(i);
			String[] points = spoint.split(" ");
			sb.append("{");
			sb.append("\"deviceID\":\""+cph+"\"");
			sb.append(",");
			sb.append("\"gpstime\":" + gpstime / 1000);
			sb.append(",");

			if(listSpeed.get(i) != 0){
				sb.append("\"speed\":"+ listSpeed.get(i));
				sb.append(",");
			}
			if(listHeading.get(i) != 0){
				sb.append("\"heading\":"+ listHeading.get(i));
				sb.append(",");
			}
			sb.append("\"longitude\":" + (int)(Double.parseDouble(points[0]) * 10000000));
			sb.append(",");
			sb.append("\"latitude\":" + (int)(Double.parseDouble(points[1]) * 10000000));
			sb.append("}");
			if (i < listPoint.size() - 1) {
				sb.append(",");
			}
		}
		sb.append("]");
		sb.append("}");

		String shapesText = HttpRequestUtil.sendPost(trackingUrl, sb.toString());
		String subPointsText = HttpRequestUtil.sendPost(subpointsUrl, sb.toString());

		//推测接口返回道路的结果
		JSONObject resultJson = JSONObject.parseObject(shapesText);
		Object state = resultJson.get("header");
		String text1 = "";
		if (state != null && state.toString().equals("CODE_200")) {
			Object data = resultJson.get("responseBody");
			text1 = new String(uncompress(Base64.getDecoder().decode(data.toString().getBytes())));
		}
		if (StringUtils.isEmpty(text1)) {
			return result;
		}
		JSONArray data = JSONArray.parseArray(text1);

		//投影点接口返回道路的结果
		JSONObject trajectoryJson = JSONObject.parseObject(subPointsText);
		Object state2 = trajectoryJson.get("header");
		String text2 = "";
		if (state2 != null && state2.toString().equals("CODE_200")) {
			Object data2 = trajectoryJson.get("responseBody");
			text2 = new String(uncompress(Base64.getDecoder().decode(data2.toString().getBytes())));
		}
		JSONArray data2 = JSONArray.parseArray(text2);
		JSONArray shapesArr1 = null;
		JSONArray subPointsArr1 = null;
		if(data2!= null && data2.size() > 0){
			JSONObject obj = JSONObject.parseObject(data2.get(0).toString());
//            System.out.println(obj.toJSONString());
			shapesArr1 = obj.getJSONArray("trajectory");          //形状点
			subPointsArr1 = obj.getJSONArray("subpoints");       //垂足点
		}

		result.put("subPoints",subPointsArr1);
		//log.info("match-response: " + text);

		int limitSpeed = 60;
		//找出道路的限速
		if(shapesArr1 != null && shapesArr1.size() > 0){
			String limit ;
			for(int x = 0; x < shapesArr1.size(); x++){
				JSONObject obj1 = (JSONObject) shapesArr1.get(x);
				if (obj1 != null) {
					String shapes = obj1.getString("shapes");
					limit = obj1.getString("limitSpeed");
					if (!limit.equals("")) {
						limitSpeed = Integer.parseInt(limit)/10;
						break;
					}
				}
			}
		}
		result.put("limitSpeed",limitSpeed);

		int totalMile = 0;
		if(shapesArr1 != null && shapesArr1.size() > 0){


			for(int i = 0; i < shapesArr1.size(); i++){
				JSONObject obj2 = (JSONObject) shapesArr1.get(i);
				if (obj2 != null) {
//					int avgSpeed = obj2.getIntValue("avgSpeed");
					String shapes = obj2.getString("shapes");

					if (!StringUtils.isEmpty(shapes)) {
						String[] shape = shapes.split(";");    //每个link的形状点
						for(int j=0; j<shape.length; j++){
							String slonlat = shape[j];
							String[] lonlat = null;
							lonlat = slonlat.split(" ");
							if(lonlat[0].contains(",")){
								lonlat = lonlat[0].replace(","," ").split(" ");
							}
							JSONArray arrTmp = new JSONArray();
							double lon = Double.parseDouble(lonlat[0]);
							double lat = Double.parseDouble(lonlat[1]);
							arrTmp.add(lon);//lon
							arrTmp.add(lat);//lat
							arrTrack.add(arrTmp);
						}
					}
				}
			}
		}
		result.put("tracks",arrTrack);
		return result;
	}




	/*
    * 解压缩数据 (Snappy)
    */
	public static   byte[] uncompress(byte[] arg0) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		GZIPInputStream input = null;
		try {
			input = new GZIPInputStream(new ByteArrayInputStream(arg0));
			IOUtils.copyLarge(input, output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}
		return output.toByteArray();
	}
	
}
