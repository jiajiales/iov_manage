package com.cennavi.vehicle_networking_data.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cennavi.vehicle_networking_data.beans.KafkaDataInfo;
import com.cennavi.vehicle_networking_data.beans.Point;
import com.cennavi.vehicle_networking_data.beans.VehicleGpsPoint;
import com.cennavi.vehicle_networking_data.dao.KafkaDataManageDao;
import com.cennavi.vehicle_networking_data.utils.CoordinateTransformUtils;
import com.cennavi.vehicle_networking_data.utils.DecryptUtils;

@Service
public class KafkaDataManageService {
	@Autowired
	private KafkaDataManageDao KafkaDataManageao;
	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;
	@Value("${params.matchUrl}")
	String matchUrl;

	public Object findList(KafkaDataInfo kafkaDataInfo) {
		// TODO Auto-generated method stub
		return KafkaDataManageao.findList(kafkaDataInfo);
	}

	// 获取解析kafka中的数据，将所有数据入库
	public Object analyticalDataPublic(String requestParam) {
		JSONArray jsonArray4 = new JSONArray();
//		 JSONArray jsonarray = new ();
//		 JSONObject machJSONO= new JSONObject();
		JSONArray machArray = new JSONArray();
//		 JSONArray jsonarray = new JSONArray();
//		 JSONArray machArray2= new JSONArray();
		JSONArray jsonarray = new JSONArray();
//		 List<String>  list2=new  ArrayList<String>();
		jsonArray4 = JSONArray.parseArray(requestParam);
		List<VehicleGpsPoint> list = new ArrayList<VehicleGpsPoint>();

		for (int i = 0; i < jsonArray4.size(); i++) {
			JSONObject job4 = jsonArray4.getJSONObject(i);
			Point point = new Point(Double.parseDouble(job4.get("JD").toString()),
					Double.parseDouble(job4.get("WD").toString()));
			Point bd09ToWgs84 = CoordinateTransformUtils.bd09ToWgs84(point.getLng(), point.getLat());

			JSONObject node = new JSONObject();
			node.put("longitude", Math.round(bd09ToWgs84.getLng() * 10000000));
			node.put("latitude", Math.round(bd09ToWgs84.getLat() * 10000000));
			jsonarray.add(node);

//				    machArray=  (JSONArray) DecryptUtils.getArry(jsonarray.toString());
//				    if(machArray!=null) {
//				    	 JSONObject machJSONO2=  (JSONObject) machArray.get(0);
//				    	  //过滤测试未匹配的代码
//						    if (machJSONO2.get("match").equals(1)) {
//						    	String str=(String) machJSONO2.get("point");
//								String [] split = str.split(",");
//						    	  String str2="POINT("+bd09ToWgs84.getLng()+" "+bd09ToWgs84.getLat()+")-"+"POINT("+split[0]+" "+split[1]+")";
////						    	machArray2.add(node2);
//						    	 list2.add(str2);
//						    }
//				    }
//				   
//				    System.out.println("machArray:"+machArray);
//				    machJSONO=  (JSONObject) machArray.get(0);

		}

		machArray = (JSONArray) DecryptUtils.getArry(jsonarray.toString(), matchUrl);
//		  if(0==0) {
//			  return list2;
//		  }

		for (int i = 0; i < jsonArray4.size(); i++) {
			JSONObject job4 = jsonArray4.getJSONObject(i);
			VehicleGpsPoint vehicleGpsPoint = new VehicleGpsPoint();
			Point point = new Point(Double.parseDouble(job4.get("JD").toString()),
					Double.parseDouble(job4.get("WD").toString()));
			Point bd09ToWgs84 = CoordinateTransformUtils.bd09ToWgs84(point.getLng(), point.getLat());
			if (!machArray.getJSONObject(i).get("point").equals("")) {
				String str = (String) machArray.getJSONObject(i).get("point");
				String[] split = str.split(",");
				vehicleGpsPoint.setJD(Double.valueOf(split[0]));
				vehicleGpsPoint.setWD(Double.valueOf(split[1]));
				vehicleGpsPoint.setMATCH(1);
			} else {
				vehicleGpsPoint.setJD(bd09ToWgs84.getLng());
				vehicleGpsPoint.setWD(bd09ToWgs84.getLat());
				vehicleGpsPoint.setMATCH(0);
			}
			vehicleGpsPoint.setCP_HM(job4.get("HPHM").toString());
			vehicleGpsPoint.setSD(job4.get("SD").toString());
			vehicleGpsPoint.setGPS_SJ(job4.get("DWSJ").toString());
			vehicleGpsPoint.setRFID_ID(job4.get("CLID").toString());
			vehicleGpsPoint.setGPS_ZT((Integer) job4.get("ZT"));
			vehicleGpsPoint.setFX(job4.get("FX").toString());
			list.add(vehicleGpsPoint);

		}
//		  
		analyticalData(list, 2);
		analyticalDataSS(list, 2);
		return list;

	}

	JSONArray jsonarrayHW = new JSONArray();

	public Object analyticaDataHWs(String requestParam) {

//		 
		String str1 = StringEscapeUtils.unescapeJava(requestParam);
		str1 = str1.substring(1, str1.length());
		str1 = str1.substring(0, str1.length() - 1);

		JSONObject json = JSONObject.parseObject(str1);
		jsonarrayHW.add(json);
		System.err.println(jsonarrayHW.size());
		if (jsonarrayHW.size() >= 100) {
			jsonarrayHW = new JSONArray();
//	    	analyticaDataHW(jsonarrayHW.toString());

		}
		return null;

	}

	public Object analyticaDataHW(String requestParam) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONArray jsonArray4 = new JSONArray();
		JSONArray machArray = new JSONArray();
		JSONArray jsonarray = new JSONArray();
		jsonArray4 = JSONArray.parseArray(requestParam);
		List<VehicleGpsPoint> list = new ArrayList<VehicleGpsPoint>();

		for (int i = 0; i < jsonArray4.size(); i++) {
			JSONObject job4 = jsonArray4.getJSONObject(i);
			JSONObject job5 = (JSONObject) job4.get("params");
//			  System.out.println("job5:"+job5);				 
			JSONObject node = new JSONObject();
			node.put("longitude", Math.round(Double.parseDouble(job5.get("gpsLongitude").toString()) * 10000000));
			node.put("latitude", Math.round(Double.parseDouble(job5.get("gpsLatitude").toString()) * 10000000));
			jsonarray.add(node);
		}
		machArray = (JSONArray) DecryptUtils.getArry(jsonarray.toString(), matchUrl);
		for (int i = 0; i < jsonArray4.size(); i++) {
			JSONObject job4 = jsonArray4.getJSONObject(i);
			VehicleGpsPoint vehicleGpsPoint = new VehicleGpsPoint();
			JSONObject job5 = (JSONObject) job4.get("params");
			if (!machArray.getJSONObject(i).get("point").equals("")) {
				String str = (String) machArray.getJSONObject(i).get("point");
				String[] split = str.split(",");
				vehicleGpsPoint.setJD(Double.valueOf(split[0]));
				vehicleGpsPoint.setWD(Double.valueOf(split[1]));
				vehicleGpsPoint.setMATCH(1);
			} else {
				vehicleGpsPoint.setJD(Double.parseDouble(job5.get("gpsLongitude").toString()));
				vehicleGpsPoint.setWD(Double.parseDouble(job5.get("gpsLatitude").toString()));
				vehicleGpsPoint.setMATCH(0);
			}
			vehicleGpsPoint.setSD(job5.get("speed").toString());
			Long timeStamp2 = (Long) job5.get("gpsTime"); // 获取当前时间戳
			vehicleGpsPoint.setGPS_SJ(sdf.format(timeStamp2));
			vehicleGpsPoint.setRFID_ID(job5.get("guid").toString());
			list.add(vehicleGpsPoint);

		}

		analyticalData(list, 3);
		analyticalDataSS(list, 3);
		return null;
	}

	public Object analyticalDataSS(List<VehicleGpsPoint> list, Integer i) {
		if (i == 3) {
			list = KafkaDataManageao.findHWCp(list);
			for (VehicleGpsPoint vehicleGpsPoint : list) {
				if (vehicleGpsPoint.getCP_HM() == null || vehicleGpsPoint.equals("")) {
					vehicleGpsPoint.setCP_HM("notmatchCP");
				}

			}
			System.out.println(list);
		}

		List<VehicleGpsPoint> VehicleGpsPointList = new ArrayList<VehicleGpsPoint>();
		HashSet<String> set = new HashSet<String>();// 创建一个set用来去重复
		for (VehicleGpsPoint vehicleGpsPoint : list) {
			set.add(vehicleGpsPoint.getCP_HM());
		}
		List<String> list2 = new ArrayList<String>();
		list2.addAll(set);// 把set放入list中
//		  System.err.println(list2);
		Map<String, VehicleGpsPoint> linkList = new HashMap<String, VehicleGpsPoint>();
		for (String str : list2) {
			List<VehicleGpsPoint> list3 = new ArrayList<VehicleGpsPoint>();
			for (VehicleGpsPoint vehicleGpsPoint : list) {
				if (str.equals(vehicleGpsPoint.getCP_HM())) {
					list3.add(vehicleGpsPoint);
				}
			}
			if (list3.size() == 1) {
				linkList.put(str, list3.get(0));
				VehicleGpsPointList.add(list3.get(0));
			}

			if (list3.size() > 1) {
				VehicleGpsPoint vehicleGpsPoint = bubbleSort(list3);
				linkList.put(str, vehicleGpsPoint);
				VehicleGpsPointList.add(bubbleSort(list3));
			}
		}
		return KafkaDataManageao.analyticalDataSS(linkList, i);
	}

	// 将数据推送到kafaka和历史表中
	public Object analyticalData(List<VehicleGpsPoint> list, Integer i) {
		JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(list));
		if (jsonArray != null) {
			if (kafkaTemplate == null) {
				System.out.println("没有注入kafkaTemplate");
			} else {

				SimpleDateFormat fdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String data = fdate.format(new Date());// 老版本的office改成xls
				if (i == 2) {
					kafkaTemplate.send("CLWGPS-LH", data, jsonArray.toString());
				}
				if (i == 3) {
					kafkaTemplate.send("CLWGPS-HW", data, jsonArray.toString());
				}
			}
		}
		return KafkaDataManageao.analyticalData(list, i);
	}

	public Object analyticalHWSS(String requestParam, int bgroupId) {
		JSONArray jsonArray2 = new JSONArray();
		JSONArray jsonArray4 = new JSONArray();

		Map<String, Map<String, Object>> rest = new HashMap<String, Map<String, Object>>();
		jsonArray4 = JSONArray.parseArray(requestParam);
		List<VehicleGpsPoint> list = new ArrayList<VehicleGpsPoint>();
		List<VehicleGpsPoint> VehicleGpsPointList = new ArrayList<VehicleGpsPoint>();
		for (int i = 0; i < jsonArray4.size(); i++) {
			JSONObject job4 = jsonArray4.getJSONObject(i);
			Point point = new Point(Double.parseDouble(job4.get("gpsLongitude").toString()),
					Double.parseDouble(job4.get("gpsLatitude").toString()));
			Point bd09ToWgs84 = CoordinateTransformUtils.bd09ToWgs84(point.getLng(), point.getLat());
			VehicleGpsPoint vehicleGpsPoint = new VehicleGpsPoint();
			vehicleGpsPoint.setJD(bd09ToWgs84.getLng());
			vehicleGpsPoint.setWD(bd09ToWgs84.getLat());
			vehicleGpsPoint.setCP_HM(job4.get("HPHM").toString());
			vehicleGpsPoint.setSD(job4.get("SD").toString());
			vehicleGpsPoint.setGPS_SJ(job4.get("occurTime").toString());
			vehicleGpsPoint.setRFID_ID(job4.get("CLID").toString());
			vehicleGpsPoint.setGPS_ZT((Integer) job4.get("ZT"));
			vehicleGpsPoint.setFX(job4.get("FX").toString());
			vehicleGpsPoint.setBgroupId(bgroupId);
//				vehicleGpsPoint.setBrigade(brigade);
			list.add(vehicleGpsPoint);
		}

		HashSet<String> set = new HashSet<String>();// 创建一个set用来去重复
		for (VehicleGpsPoint vehicleGpsPoint : list) {
			set.add(vehicleGpsPoint.getCP_HM());
		}
		List<String> list2 = new ArrayList<String>();
		list2.addAll(set);// 把set放入list中
//		  System.err.println(list2);
		Map<String, VehicleGpsPoint> linkList = new HashMap<String, VehicleGpsPoint>();
		for (String str : list2) {
			List<VehicleGpsPoint> list3 = new ArrayList<VehicleGpsPoint>();
			for (VehicleGpsPoint vehicleGpsPoint : list) {
				if (str.equals(vehicleGpsPoint.getCP_HM())) {
					list3.add(vehicleGpsPoint);
				}
			}
			if (list3.size() == 1) {
				linkList.put(str, list3.get(0));
				VehicleGpsPointList.add(list3.get(0));
			}

			if (list3.size() > 1) {
				VehicleGpsPoint vehicleGpsPoint = bubbleSort(list3);
				linkList.put(str, vehicleGpsPoint);
				VehicleGpsPointList.add(bubbleSort(list3));
			}
		}

//		  for (String key : linkList.keySet()) {
//		    Object value = linkList.get(key);
//		    System.out.println("Key = " + key + ", Value = " + value);
//		  }
		return KafkaDataManageao.analyticalDataSS(linkList, 2);
	}

	// 取出最新时间的数据
	public VehicleGpsPoint bubbleSort(List<VehicleGpsPoint> list) {
//		  System.err.println("__________-"+list);
		// 如果只有一个元素就不用排序了
		String LatestTime = "2019-01-01";
		String key = null;
		Map<String, VehicleGpsPoint> linkList = new HashMap<String, VehicleGpsPoint>();
		List<VehicleGpsPoint> list0 = new ArrayList<VehicleGpsPoint>();
		for (VehicleGpsPoint vehicleGpsPoint : list) {
			if (LatestTime.compareTo(vehicleGpsPoint.getGPS_SJ()) < 0) {
				LatestTime = vehicleGpsPoint.getGPS_SJ();
				key = vehicleGpsPoint.getCP_HM();
				linkList.put(vehicleGpsPoint.getCP_HM(), vehicleGpsPoint);
			}
		}
		return linkList.get(key);
	}

}
