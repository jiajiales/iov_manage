package com.cennavi.audi_data_collect.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSON;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.io.WKTReader;

/**
 * 公共变量存放工具
 * @author Admin
 *
 */
public class CommUtils {
	public static final int SUCCODE = 200;//请求成功
	public static final int ERRCODE = 500;//服务器内部错误，无法完成请求
	public static final int LOGCODE = 401;//请求要求用户的身份认证
	/**
	 * 两个时间相差距离多少天多少小时多少分多少秒
	 * @param str1 时间参数 1 格式：1990-01-01 12:00:00
	 * @param str2 时间参数 2 格式：2009-01-01 12:00:00
	 * @return long[] 返回值为：{天, 时, 分, 秒}
	 */
	public static long[] getDistanceTimes(String str1, String str2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {
			String strOne=str1.substring(0,4)+"-"+str1.substring(4,6)+"-"+str1.substring(6,8)+" "+str1.substring(8,10)+":"+str1.substring(10,12)+":"+str1.substring(12,14);
			String strTwo=str2.substring(0,4)+"-"+str2.substring(4,6)+"-"+str2.substring(6,8)+" "+str2.substring(8,10)+":"+str2.substring(10,12)+":"+str2.substring(12,14);
			one = df.parse(strOne);
			two = df.parse(strTwo);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff ;
			if(time1<time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = (diff / (60 * 60 * 1000) - day * 24);
			min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long[] times = {day, hour, min, sec};
		return times;
	}
	
	public static Map<String, Object> getGeojson(JdbcTemplate jdbc, String sql)
			throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("type", "FeatureCollection");

		List<Map<String, Object>> results = jdbc.queryForList(sql);

		List<Map<String, Object>> features = new ArrayList<Map<String, Object>>();

		Coordinate cs = null;
		for (Map<String, Object> m : results) {
			
			int hashCode = 0;
			
			if (m.containsKey("geom")) {
				hashCode = m.get("geom").hashCode();
				m.remove("geom");
			}
			Map<String, Object> feature = new HashMap<String, Object>();
			
			feature.put("type", "Feature");
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("tips_id", String.valueOf(hashCode));
			Iterator<Entry<String, Object>> it = m.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Object> en = it.next();
				if (!"wkt".equals(en.getKey())) {
					properties.put(en.getKey(), en.getValue());
				}
			}
			feature.put("properties", properties);
			//feature.put("geometry", JSON.parse(m.get("geojson").toString()));
			cs = new WKTReader().read(m.get("wkt").toString()).getCoordinates()[0];
			feature.put("geometry", JSON.parse("{\"coordinates\":["+cs.x+","+cs.y+"],\"type\":\"Point\"}"));
			features.add(feature);

		}
		map.put("features", features);

		return map;

	}
}
