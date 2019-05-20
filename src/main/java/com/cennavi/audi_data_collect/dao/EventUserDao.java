package com.cennavi.audi_data_collect.dao;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cennavi.audi_data_collect.bean.CVSBean;
import com.cennavi.audi_data_collect.bean.EventPV;
import com.cennavi.audi_data_collect.bean.ParamsBean;
@Component

public class EventUserDao {
	 @Autowired
	    private JdbcTemplate jdbcTemplate;
	 private static SimpleDateFormat inSDF = new SimpleDateFormat("yyyy-mm-dd");
	  private static SimpleDateFormat outSDF = new SimpleDateFormat("mm/dd/yyyy");
//	  private static SimpleDateFormat inSDFH = new SimpleDateFormat("hh:mm:ss");
//	  private static SimpleDateFormat outSDFH = new SimpleDateFormat("hh/mm/ss");
	 //验证用户信息111
	public boolean check(String name, String password) {
		
		System.err.println(name);
		 String sql="select count(id) from  user_info  where name= '"+name+"'  and password ='"+password+"'";
		 Integer k=jdbcTemplate.queryForObject(sql,Integer.class);
		if(k==1) {
			return true;
		}
		return false;
	}
	//类型列表
	public Object queryEventType() {
		 String sql="select * from  event_type  ";
			return  jdbcTemplate.queryForList(sql);
	 
	}
	
	 
	 
	 
	public List<CVSBean> exportCsvs(ParamsBean paramsBean) throws ParseException {
		String sql = "SELECT  a.event_id,COALESCE(b.event_name_en, '0') as type_name,d.en_name as road_name,substring(a.upload_time,1,10) AS date, substring(a.upload_time,12,8) AS time    FROM collection_info_new a   LEFT JOIN event_type b ON b.type_code=a.event_type LEFT JOIN  gaosu_segment  c  ON c.id=a.segment_id    LEFT JOIN gaosu  d  ON c.road_id=d.road_id   WHERE 1=1  ";
		System.err.println("city:" + paramsBean.getCity());
		if (!paramsBean.getCity().equals("") && paramsBean.getCity() != null) {
			sql += " and a.city_name =  '" + paramsBean.getCity() + "' ";

		}
		if (paramsBean.getEventsList().length > 0 && paramsBean.getEventsList() != null) {
			String eventsList = "";
			for (int i = 0; i < paramsBean.getEventsList().length; i++) {
				eventsList = eventsList + "'" + paramsBean.getEventsList()[i] + "',";
			}
			if (eventsList.length() > 0) {
				eventsList = eventsList.substring(0, eventsList.length() - 1);
			}
			sql += " and a.event_type IN ( " + eventsList + " )";
		}

		if (paramsBean.getIsContinuous().equals("true")) {
			if (paramsBean.getDataList().length > 0) {
				if (!paramsBean.getDataList()[0].equals("") && paramsBean.getDataList()[0] != null
						&& !paramsBean.getDataList()[1].equals("") && paramsBean.getDataList()[1] != null) {
					sql += " AND to_timestamp(a.upload_time,'yyyy-MM-dd')>='" + paramsBean.getDataList()[0]
							+ "' and to_timestamp(a.upload_time,'yyyy-MM-dd') <=  '" + paramsBean.getDataList()[1]
							+ "' ";
				}
			}

		} else {
			if (paramsBean.getDataList().length > 0) {
				String dataLists = "";
				for (int i = 0; i < paramsBean.getDataList().length; i++) {
					dataLists = dataLists + "'" + paramsBean.getDataList()[i] + "',";
				}
				if (dataLists.length() > 0) {
					dataLists = dataLists.substring(0, dataLists.length() - 1);
				}
				sql += "AND  (to_timestamp(a.upload_time,'yyyy-MM-dd') in (" + dataLists + ") )";
			}
		}
		if (paramsBean.getTimeFrame().length > 0) {

			if (!paramsBean.getTimeFrame()[0].equals("") && paramsBean.getTimeFrame()[0] != null
					&& !paramsBean.getTimeFrame()[1].equals("") && paramsBean.getTimeFrame()[1] != null) {
				sql += " and substring(a.upload_time,12,5)>= '" + paramsBean.getTimeFrame()[0]
						+ "' and substring(a.upload_time,12,5)<= '" + paramsBean.getTimeFrame()[1] + "'";

			}
		}

		if (paramsBean.getRoadSecList().length > 0 && paramsBean.getRoadSecList() != null) {

			String roadSecList = "";
			for (int i = 0; i < paramsBean.getRoadSecList().length; i++) {
				roadSecList = roadSecList + "'" + paramsBean.getRoadSecList()[i] + "',";
			}
			if (roadSecList.length() > 0) {
				roadSecList = roadSecList.substring(0, roadSecList.length() - 1);
			}
			sql += "and d.r_id IN  ( " + roadSecList + " ) ";
		}
		System.err.println("sql:" + sql);
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
		List<CVSBean> list = new ArrayList();
		for (Map<String, Object> map : queryForList) { // 将数据转换成json格式
			CVSBean cVSBean = new CVSBean();
			for (String k : map.keySet()) {
				if (k.equals("event_id")) {
					cVSBean.setEvent_id((Integer) map.get(k));
				}
				if (k.equals("type_name")) {
					cVSBean.setType_name(map.get(k).toString());
				}
				if (k.equals("road_name")) {
					cVSBean.setRoad_name(map.get(k).toString());
				}
				if (k.equals("date")) { // 日期格式转换
					String outDate = "";
					Date date = inSDF.parse(map.get(k).toString());
					outDate = outSDF.format(date);
					cVSBean.setDate(outDate);
				}
				if (k.equals("time")) {// 时间格式转换
//					   String outHour = "";
//					    Date date = inSDFH.parse(map.get(k).toString());
//			            outHour = outSDFH.format(date);
					cVSBean.setTime(map.get(k).toString());
				}

			}
			list.add(cVSBean);

		}
		return list;
	}
	
//	数据统计
	public Object dataStatistic(ParamsBean paramsBean) throws Exception {
		String sql = "SELECT  t1.event_name_en as typeName ,t1.type_code as typeCode,COALESCE(t2.nums,0) AS  num    from event_type t1  LEFT JOIN  ( SELECT COALESCE(count(a.event_id), 0) as nums  , b.type_name as eventName  FROM  collection_info_new   a   LEFT JOIN   event_type b    ON a.event_type=b.type_code LEFT JOIN  gaosu_segment  c  ON c.id=a.segment_id LEFT JOIN gaosu  d  ON c.road_id=d.road_id WHERE 1=1";
		String sql2 = "SELECT COALESCE(count(a.event_id), 0) as nums   FROM event_type b    LEFT JOIN   collection_info_new   a   ON a.event_type=b.type_code LEFT JOIN  gaosu_segment  c   ON c.id=a.segment_id  LEFT JOIN gaosu  d  ON c.road_id=d.road_id  WHERE 1=1  ";

		if (paramsBean.getCity() != null && !paramsBean.getCity().equals("")) {
			sql += " and a.city_name='" + paramsBean.getCity() + "'";
			sql2 += " and a.city_name='" + paramsBean.getCity() + "'";
		}

		if (paramsBean.getDataList() != null) {
			if (paramsBean.getIsContinuous().equals("true")) {

				if (paramsBean.getDataList().length > 0) {
					sql += " and substring(a.upload_time,0,11) between '" + paramsBean.getDataList()[0] + "' and '"
							+ paramsBean.getDataList()[1] + "'";
					sql2 += " and substring(a.upload_time,0,11) between '" + paramsBean.getDataList()[0] + "' and '"
							+ paramsBean.getDataList()[1] + "'";

				}
			} else {
				String es2 = "(";
				for (int i = 0; i < paramsBean.getDataList().length; i++) {
					es2 += "'" + paramsBean.getDataList()[i] + "',";
				}
				es2 = es2.substring(0, es2.length() - 1) + ")";
				if (paramsBean.getDataList().length > 0) {
					sql += " and substring(a.upload_time,0,11) in " + es2;
					sql2 += " and substring(a.upload_time,0,11) in " + es2;
				}

			}
		}

		if (paramsBean.getEventsList() != null) {
			String es = "(";
			for (int i = 0; i < paramsBean.getEventsList().length; i++) {
				es += "'" + paramsBean.getEventsList()[i] + "',";
			}
			es = es.substring(0, es.length() - 1) + ")";
			if (paramsBean.getEventsList().length > 0) {
				sql += " and a.event_type in " + es;
				sql2 += " and a.event_type in " + es;
			}

		}

		if (paramsBean.getTimeFrame() != null) {
			if (paramsBean.getTimeFrame().length > 0) {
				sql += " and substring(a.upload_time,12,5) between '" + paramsBean.getTimeFrame()[0] + "' and '"
						+ paramsBean.getTimeFrame()[1] + "'";
				sql2 += " and substring(a.upload_time,12,5) between '" + paramsBean.getTimeFrame()[0] + "' and '"
						+ paramsBean.getTimeFrame()[1] + "'";
			}
		}
		if (paramsBean.getRoadSecList() != null) {
			String es1 = "(";
			for (int i = 0; i < paramsBean.getRoadSecList().length; i++) {
				es1 += paramsBean.getRoadSecList()[i] + ",";
			}
			es1 = es1.substring(0, es1.length() - 1) + ")";
			if (paramsBean.getRoadSecList().length > 0) {
				sql += " and d.r_id in " + es1;
				sql2 += " and d.r_id in " + es1;
			}
		}

		sql += " group  by b.type_name   )  t2  ON t2.eventName=t1.type_name ";

		if (paramsBean.getSort() != null && !paramsBean.getSort().equals("")) {
			sql += "  order by  num  " + paramsBean.getSort() + "";
		}
		System.err.println("sql:" + sql);
		System.err.println("sql2:" + sql2);
		double k = jdbcTemplate.queryForObject(sql2, double.class);
		System.out.println("k:" + k);

		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
		List<Map<String, Object>> queryForList2 = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : queryForList) {
			Map<String, Object> map2 = new HashMap<String, Object>();
			for (String s : map.keySet()) {
				map2.put(s, map.get(s));
				if (s.equals("num")) {
					double a = Integer.parseInt(map.get(s).toString());
					double n = (double) Math.round(a / k * 10000) / 100;
					if (n == 100.00) {
						n = 99.99;
					}
					map2.put("percentage", n);
				}
			}

			queryForList2.add(map2);
		}
		JSONObject json;
		JSONArray jsonArray = new JSONArray();
		for (Map<String, Object> map : queryForList2) {
			json = new JSONObject(map);
			jsonArray.add(json);
		}

		return jsonArray;
	}

	public Object brokenLines(ParamsBean paramsBean) {
		String sql = "SELECT count(*) as mycount,myhour,road_name,road_id FROM (   SELECT  a.upload_time ,d.en_name as road_name,d.r_id as road_id, date_part('hour',to_timestamp(a.upload_time,'yyyy-MM-dd hh24:mi:ss')) as myhour   FROM  collection_info_new a   left join gaosu_segment b  on a.segment_id=b.id  LEFT JOIN gaosu  d  ON b.road_id=d.road_id where 1=1";
		String sql2 = "SELECT count(event_id) as num   FROM collection_info_new a  LEFT JOIN  gaosu_segment  c  ON c.id=a.segment_id   LEFT JOIN gaosu  d  ON c.road_id=d.road_id   WHERE 1=1";

		if (!paramsBean.getCity().equals("") && paramsBean.getCity() != null) {
			sql += " and a.city_name =  '" + paramsBean.getCity() + "' ";
			sql2 += " and a.city_name =  '" + paramsBean.getCity() + "' ";
		}
		if (!paramsBean.getEventType().equals("") && paramsBean.getEventType() != null) {
			sql += " and a.event_type =  '" + paramsBean.getEventType() + "' ";
			sql2 += " and  a.event_type =  '" + paramsBean.getEventType() + "' ";
		}

		if (paramsBean.getDataList() != null) {
			if (paramsBean.getIsContinuous().equals("true")) {

				if (paramsBean.getDataList().length > 0) {
					sql += " and substring(a.upload_time,0,11) between '" + paramsBean.getDataList()[0] + "' and '"
							+ paramsBean.getDataList()[1] + "'";
					sql2 += " and substring(a.upload_time,0,11) between '" + paramsBean.getDataList()[0] + "' and '"
							+ paramsBean.getDataList()[1] + "'";

				}
			} else {
				String es2 = "(";
				for (int i = 0; i < paramsBean.getDataList().length; i++) {
					es2 += "'" + paramsBean.getDataList()[i] + "',";
				}
				es2 = es2.substring(0, es2.length() - 1) + ")";
				if (paramsBean.getDataList().length > 0) {
					sql += " and substring(a.upload_time,0,11) in " + es2;
					sql2 += " and substring(a.upload_time,0,11) in " + es2;
				}

			}
		}

		if (paramsBean.getTimeFrame() != null) {
			if (paramsBean.getTimeFrame().length > 0) {
				sql += " and substring(a.upload_time,12,5) between '" + paramsBean.getTimeFrame()[0] + "' and '"
						+ paramsBean.getTimeFrame()[1] + "'";
				sql2 += " and substring(a.upload_time,12,5) between '" + paramsBean.getTimeFrame()[0] + "' and '"
						+ paramsBean.getTimeFrame()[1] + "'";
			}
		}
		if (paramsBean.getRoadSecList() != null) {
			String es1 = "(";
			for (int i = 0; i < paramsBean.getRoadSecList().length; i++) {
				es1 += paramsBean.getRoadSecList()[i] + ",";
			}
			es1 = es1.substring(0, es1.length() - 1) + ")";
			if (paramsBean.getRoadSecList().length > 0) {
				sql += " and d.r_id in " + es1;
				sql2 += " and d.r_id in " + es1;
			}
		}

		sql += ") as hour_table GROUP BY  myhour,road_name,road_id   ORDER BY myhour ASC";
		System.err.println("sql:" + sql);
		System.err.println("sql2:" + sql2);
		double k = jdbcTemplate.queryForObject(sql2, double.class);
		System.out.println("k:" + k);
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
		List<Map<String, Object>> queryForList2 = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> map : queryForList) {
			Map<String, Object> map2 = new HashMap<String, Object>();
			for (String s : map.keySet()) {
				map2.put(s, map.get(s));
				if (s.equals("mycount")) {
					double a = Integer.parseInt(map.get(s).toString());
					double n = (double) Math.round(a / k * 10000) / 100;
					if (n == 100.00) {
						n = 99.99;
					}
					map2.put("percentage", n);
				}
			}

			queryForList2.add(map2);
		}
		JSONObject json;
		JSONArray jsonArray = new JSONArray();
		for (Map<String, Object> map : queryForList2) { // 将数据转换成json格式
			json = new JSONObject(map);
			jsonArray.add(json);
		}

		return jsonArray;
	}

	public Object queryHistograms(ParamsBean paramsBean) {
		String sql = "SELECT count(*) as mycount,myhour FROM (  SELECT  upload_time , date_part('hour',to_timestamp(upload_time,'yyyy-MM-dd hh24:mi:ss')) as myhour  FROM  collection_info_new a  LEFT JOIN  gaosu_segment  c  ON c.id=a.segment_id  LEFT JOIN gaosu  d  ON c.road_id=d.road_id   WHERE  1=1";
		String sql2 = "SELECT count(event_id) as num   FROM collection_info_new a LEFT JOIN  gaosu_segment  c  ON c.id=a.segment_id   LEFT JOIN gaosu  d  ON c.road_id=d.road_id WHERE  1=1";
		if (!paramsBean.getCity().equals("") && paramsBean.getCity() != null) {
			sql += " and a.city_name =  '" + paramsBean.getCity() + "' ";
			sql2 += " and a.city_name =  '" + paramsBean.getCity() + "' ";
		}
		if (!paramsBean.getEventType().equals("") && paramsBean.getEventType() != null) {
			sql += " and a.event_type =  '" + paramsBean.getEventType() + "' ";
			sql2 += " and  a.event_type =  '" + paramsBean.getEventType() + "' ";
		}

		if (paramsBean.getDataList() != null) {
			if (paramsBean.getIsContinuous().equals("true")) {

				if (paramsBean.getDataList().length > 0) {
					sql += " and substring(a.upload_time,0,11) between '" + paramsBean.getDataList()[0] + "' and '"
							+ paramsBean.getDataList()[1] + "'";
					sql2 += " and substring(a.upload_time,0,11) between '" + paramsBean.getDataList()[0] + "' and '"
							+ paramsBean.getDataList()[1] + "'";

				}
			} else {
				String es2 = "(";
				for (int i = 0; i < paramsBean.getDataList().length; i++) {
					es2 += "'" + paramsBean.getDataList()[i] + "',";
				}
				es2 = es2.substring(0, es2.length() - 1) + ")";
				if (paramsBean.getDataList().length > 0) {
					sql += " and substring(a.upload_time,0,11) in " + es2;
					sql2 += " and substring(a.upload_time,0,11) in " + es2;
				}

			}
		}

		if (paramsBean.getTimeFrame() != null) {
			if (paramsBean.getTimeFrame().length > 0) {
				sql += " and substring(a.upload_time,12,5) between '" + paramsBean.getTimeFrame()[0] + "' and '"
						+ paramsBean.getTimeFrame()[1] + "'";
				sql2 += " and substring(a.upload_time,12,5) between '" + paramsBean.getTimeFrame()[0] + "' and '"
						+ paramsBean.getTimeFrame()[1] + "'";
			}
		}
		if (paramsBean.getRoadSecList() != null) {
			String es1 = "(";
			for (int i = 0; i < paramsBean.getRoadSecList().length; i++) {
				es1 += paramsBean.getRoadSecList()[i] + ",";
			}
			es1 = es1.substring(0, es1.length() - 1) + ")";
			if (paramsBean.getRoadSecList().length > 0) {
				sql += " and d.r_id in " + es1;
				sql2 += " and d.r_id in " + es1;
			}
		}

		sql += ") as hour_table GROUP BY  myhour ORDER BY myhour ASC";

		System.err.println("sql:" + sql);
		System.err.println("sql2:" + sql2);

		double k = jdbcTemplate.queryForObject(sql2, double.class); // 获取总数
		System.out.println("k:" + k);
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
		List<Map<String, Object>> queryForList2 = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : queryForList) {
			Map<String, Object> map2 = new HashMap<String, Object>();
			for (String s : map.keySet()) {
				map2.put(s, map.get(s));
				if (s.equals("mycount")) {
					double a = Integer.parseInt(map.get(s).toString());
					double n = (double) Math.round(a / k * 10000) / 100;
					if (n == 100.00) {
						n = 99.99;
					}
					map2.put("percentage", n);
				}
			}

			queryForList2.add(map2);
		}
		JSONObject json;
		JSONArray jsonArray = new JSONArray();
		for (Map<String, Object> map : queryForList2) {// 将map转成json格式
			json = new JSONObject(map);
			jsonArray.add(json);
		}

		return jsonArray;
	}
	
	//图片
	public Object findImages(ParamsBean paramsBean) throws IOException {
		String sql = "SELECT count(a.event_id) FROM event_type b   LEFT JOIN   collection_info_new   a   ON a.event_type=b.type_code  LEFT JOIN  gaosu_segment  c   ON c.id=a.segment_id  LEFT JOIN gaosu  d  ON c.road_id=d.road_id  LEFT JOIN event_images_info e ON e.event_id =a.event_id  WHERE 1=1 ";
		String sql2 = "SELECT a.event_id ,a.lon ,a.lat,b.event_name_en as event_type,a.upload_time as date,d.en_name as route,e.description FROM event_type b   LEFT JOIN   collection_info_new   a   ON a.event_type=b.type_code  LEFT JOIN  gaosu_segment  c   ON c.id=a.segment_id  LEFT JOIN gaosu  d  ON c.road_id=d.road_id  LEFT JOIN event_images_info e ON e.event_id =a.event_id  WHERE 1=1 ";
		if (paramsBean.getEventId() != null && !paramsBean.getEventId().equals("")) {
			sql2 += " and a.event_id=" + paramsBean.getEventId() + "";
			JSONObject json;
			JSONArray jsonArray = new JSONArray();
			List<Map<String, Object>> queryForList0 = jdbcTemplate.queryForList(sql2);

			for (Map<String, Object> map : queryForList0) { // 将数据转换成json格式
				json = new JSONObject(map);
				jsonArray.add(json);
			}
			return jsonArray;
		} else {

			if (paramsBean.getCity() != null && !paramsBean.getCity().equals("")) {
				sql += " and a.city_name='" + paramsBean.getCity() + "'";
				sql2 += " and a.city_name='" + paramsBean.getCity() + "'";
			}

			if (paramsBean.getDataList() != null) {
				if (paramsBean.getIsContinuous().equals("true")) {

					if (paramsBean.getDataList().length > 0) {
						sql += " and substring(a.upload_time,0,11) between '" + paramsBean.getDataList()[0] + "' and '"
								+ paramsBean.getDataList()[1] + "'";

						sql2 += " and substring(a.upload_time,0,11) between '" + paramsBean.getDataList()[0] + "' and '"
								+ paramsBean.getDataList()[1] + "'";

					}
				} else {
					String es2 = "(";
					for (int i = 0; i < paramsBean.getDataList().length; i++) {
						es2 += "'" + paramsBean.getDataList()[i] + "',";
					}
					es2 = es2.substring(0, es2.length() - 1) + ")";
					if (paramsBean.getDataList().length > 0) {
						sql += " and substring(a.upload_time,0,11) in " + es2;
						sql2 += " and substring(a.upload_time,0,11) in " + es2;
					}

				}
			}

			if (paramsBean.getEventsList() != null) {
				String es = "(";
				for (int i = 0; i < paramsBean.getEventsList().length; i++) {
					es += "'" + paramsBean.getEventsList()[i] + "',";
				}
				es = es.substring(0, es.length() - 1) + ")";
				if (paramsBean.getEventsList().length > 0) {
					sql += " and a.event_type in " + es;
					sql2 += " and a.event_type in " + es;
				}

			}

			if (paramsBean.getTimeFrame() != null) {
				if (paramsBean.getTimeFrame().length > 0) {
					sql += " and substring(a.upload_time,12,5) between '" + paramsBean.getTimeFrame()[0] + "' and '"
							+ paramsBean.getTimeFrame()[1] + "'";
					sql2 += " and substring(a.upload_time,12,5) between '" + paramsBean.getTimeFrame()[0] + "' and '"
							+ paramsBean.getTimeFrame()[1] + "'";
				}
			}
			if (paramsBean.getRoadSecList() != null) {
				String es1 = "(";
				for (int i = 0; i < paramsBean.getRoadSecList().length; i++) {
					es1 += paramsBean.getRoadSecList()[i] + ",";
				}
				es1 = es1.substring(0, es1.length() - 1) + ")";
				if (paramsBean.getRoadSecList().length > 0) {
					sql += " and d.r_id in " + es1;
					sql2 += " and d.r_id in " + es1;
				}
			}
			if (paramsBean.getSort() != null && !paramsBean.getSort().equals("")) {
			}
			sql2 += "ORDER BY a.upload_time  " + paramsBean.getSort() + "";
			Integer n = jdbcTemplate.queryForObject(sql, Integer.class);

			if (paramsBean.getLimit() != null && paramsBean.getOffset() != null) {
				sql2 += " LIMIT " + paramsBean.getLimit() + " OFFSET " + paramsBean.getOffset() + "";
			}

			System.err.println("sql2:" + sql2);

			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql2);
			List<Map<String, Object>> queryForList2 = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> map : queryForList) {
				Map<String, Object> map2 = new HashMap<String, Object>();
				for (String s : map.keySet()) {
					map2.put(s, map.get(s));
					if (s.equals("event_id")) {

						String filePath = "http://117.51.149.90/images/" + map.get(s) + ".png";
						URL url = new URL(filePath);
						HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
						String message = urlcon.getHeaderField(0);
						if (StringUtils.hasText(message) && message.startsWith("HTTP/1.1 404")) {
							String filePath2 = "http://117.51.149.90/images/" + map.get(s) + ".jpg";
							URL url2 = new URL(filePath2);
							HttpURLConnection urlcon2 = (HttpURLConnection) url2.openConnection();
							String message2 = urlcon2.getHeaderField(0);
							if (StringUtils.hasText(message2) && message2.startsWith("HTTP/1.1 404")) {
								map2.put("imageUrl", "");
							} else {
								map2.put("imageUrl", filePath2);
							}

						} else {
							map2.put("imageUrl", filePath);
						}

//					 map2.put("videoUrl",  "http://117.51.149.90/videos/"+map.get(s)+".mp4");
					}
				}

				queryForList2.add(map2);
			}

			JSONObject json;
			JSONArray jsonArray = new JSONArray();
			for (Map<String, Object> map : queryForList2) {// 将map转成json格式
				json = new JSONObject(map);
				jsonArray.add(json);
			}

			Map<String, Object> map3 = new HashMap<String, Object>();
			map3.put("num", n);
			map3.put("data", queryForList2);
			JSONObject json2 = new JSONObject(map3);
			jsonArray.add(json2);

			return json2;
		}

	}

	public Object editImageDescription(EventPV eventPV) {

		JSONObject json;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("message", "add description failure");
		if (eventPV.getEventId() != null && !eventPV.getEventId().equals("")) {

			String sql0 = "SELECT count(id) FROM event_images_info WHERE  event_id=" + eventPV.getEventId() + "";
			int i = jdbcTemplate.queryForObject(sql0, Integer.class);
			if (i == 0) {
				String sql = "INSERT INTO  event_images_info  (id,event_id,description,upload_time) VALUES (?,?,?,?)";
				SimpleDateFormat fdate = new SimpleDateFormat("yyyyMMddHHmmss");
				String str = fdate.format(new Date());
				jdbcTemplate.update(sql, str, eventPV.getEventId(), eventPV.getDescription(), "LOCALTIMESTAMP (0)");
				map.put("message", "add description success");

			}
			if (i == 1) {
				String sql2 = "UPDATE event_images_info	SET description=? 	WHERE event_id=?";
				jdbcTemplate.update(sql2, eventPV.getDescription(), eventPV.getEventId());
				map.put("message", "edit description success");
			}

		}
		json = new JSONObject(map);
		return json;

	}

	public Object addVideoComment(EventPV eventPV) {
		JSONObject json;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("message", "add comment failure");
		if (eventPV.getEventId() != null && eventPV.getComment() != null) {
			String sql = "INSERT INTO  event_videos_info  (id,event_id,comment,upload_time) VALUES (?,?,?,?)";

			SimpleDateFormat fdate = new SimpleDateFormat("yyyyMMddHHmmss");
			String str = fdate.format(new Date());
			eventPV.setUploadTime("LOCALTIMESTAMP (0)");
			jdbcTemplate.update(sql, str, eventPV.getEventId(), eventPV.getComment(), eventPV.getUploadTime());
			map.put("message", "add comment success");
		}
		json = new JSONObject(map);
		return json;
	}

	public Object findVideoCommentList(EventPV eventPV) {

		if (eventPV.getEventId() != null) {
			String sql = "SELECT * FROM event_videos_info WHERE  event_id=" + eventPV.getEventId() + "";
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);

//		 List< Map<String,Object>> queryForList2 = new ArrayList<Map<String,Object>>();
//		  for (Map<String, Object> map : queryForList) {
//			  Map<String, Object> map2=  new HashMap<String, Object>();
//				 for (String s : map.keySet()) {
//					 map2.put(s, map.get(s));
//		            }
//				 
//				 queryForList2.add(map2);
//		}
			JSONObject json;
			JSONArray jsonArray = new JSONArray();
			for (Map<String, Object> map : queryForList) {// 将map转成json格式
				json = new JSONObject(map);
				jsonArray.add(json);
			}
			return jsonArray;
		}

		return false;
	}

	public List<EventPV> exportCsv() {

		String sql = "SELECT event_id   ,LOCALTIMESTAMP (0) as upload_time FROM collection_info_new  WHERE substring(upload_time,0,11) in ('2019-04-08')";
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);

		List<EventPV> list = new ArrayList<EventPV>();
		List<Map<String, Object>> queryForList2 = new ArrayList<Map<String, Object>>();
		int i = 1;
		for (Map<String, Object> map : queryForList) {
			Map<String, Object> map2 = new HashMap<String, Object>();

			for (String s : map.keySet()) {
				map2.put(s, map.get(s));
				if (s.equals("event_id")) {
					map2.put("comment", "video" + map.get(s) + "comment");
					map2.put("id", i);
					i++;
				}
			}

			queryForList2.add(map2);
		}
		System.err.println(i);
		for (Map<String, Object> map : queryForList2) {
			EventPV eventPV = new EventPV();
			for (String s : map.keySet()) {

				if (s.equals("id")) {
					eventPV.setId(map.get(s).toString());

				}
				if (s.equals("event_id")) {
					eventPV.setEventId((Integer) map.get(s));
//					  System.err.println((Integer) map.get(s));
				}
				if (s.equals("comment")) {
					eventPV.setComment(map.get(s).toString());
					;
//					  System.err.println(map.get(s).toString());
				}
				if (s.equals("upload_time")) {
					eventPV.setUploadTime(map.get(s).toString());
				}

			}
			System.err.println("id:" + eventPV.getId() + "EventId:" + eventPV.getEventId() + "Description:"
					+ eventPV.getDescription() + "UploadTime:" + eventPV.getUploadTime());
			list.add(eventPV);
		}
		System.err.println(list.size());
		return list;
	}

	public List<String> findImagesUrl(ParamsBean paramsBean) throws IOException {
		String sql2 = "SELECT a.event_id FROM event_type b   LEFT JOIN   collection_info_new   a   ON a.event_type=b.type_code  LEFT JOIN  gaosu_segment  c   ON c.id=a.segment_id  LEFT JOIN gaosu  d  ON c.road_id=d.road_id  LEFT JOIN event_images_info e ON e.event_id =a.event_id  WHERE 1=1 ";

		if (paramsBean.getCity() != null && !paramsBean.getCity().equals("")) {
			sql2 += " and a.city_name='" + paramsBean.getCity() + "'";
		}

		if (paramsBean.getDataList() != null) {
			if (paramsBean.getIsContinuous().equals("true")) {

				if (paramsBean.getDataList().length > 0) {

					sql2 += " and substring(a.upload_time,0,11) between '" + paramsBean.getDataList()[0] + "' and '"
							+ paramsBean.getDataList()[1] + "'";

				}
			} else {
				String es2 = "(";
				for (int i = 0; i < paramsBean.getDataList().length; i++) {
					es2 += "'" + paramsBean.getDataList()[i] + "',";
				}
				es2 = es2.substring(0, es2.length() - 1) + ")";
				if (paramsBean.getDataList().length > 0) {
					sql2 += " and substring(a.upload_time,0,11) in " + es2;
				}

			}
		}

		if (paramsBean.getEventsList() != null) {
			String es = "(";
			for (int i = 0; i < paramsBean.getEventsList().length; i++) {
				es += "'" + paramsBean.getEventsList()[i] + "',";
			}
			es = es.substring(0, es.length() - 1) + ")";
			if (paramsBean.getEventsList().length > 0) {
				sql2 += " and a.event_type in " + es;
			}

		}

		if (paramsBean.getTimeFrame() != null) {
			if (paramsBean.getTimeFrame().length > 0) {
				sql2 += " and substring(a.upload_time,12,5) between '" + paramsBean.getTimeFrame()[0] + "' and '"
						+ paramsBean.getTimeFrame()[1] + "'";
			}
		}
		if (paramsBean.getRoadSecList() != null) {
			String es1 = "(";
			for (int i = 0; i < paramsBean.getRoadSecList().length; i++) {
				es1 += paramsBean.getRoadSecList()[i] + ",";
			}
			es1 = es1.substring(0, es1.length() - 1) + ")";
			if (paramsBean.getRoadSecList().length > 0) {
				sql2 += " and d.r_id in " + es1;
			}
		}

		sql2 += "ORDER BY a.upload_time  ASC";

		System.err.println("sql2:" + sql2);
		List<String> list = new ArrayList<String>();

		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql2);
		for (Map<String, Object> map : queryForList) {
			for (String s : map.keySet()) {
				if (s.equals("event_id")) {
					String filePath = "http://117.51.149.90/images/" + map.get(s) + ".png";
					URL url = new URL(filePath);
					HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
					String message = urlcon.getHeaderField(0);
					if (StringUtils.hasText(message) && message.startsWith("HTTP/1.1 404")) {
						String filePath2 = "http://117.51.149.90/images/" + map.get(s) + ".jpg";
						list.add(filePath2);
					} else {
						list.add(filePath);
					}

				}
			}

		}

		return list;
	}

	public List<String> findVideosUrl(ParamsBean paramsBean) throws IOException {
		String sql2 = "SELECT a.event_id FROM event_type b   LEFT JOIN   collection_info_new   a   ON a.event_type=b.type_code  LEFT JOIN  gaosu_segment  c   ON c.id=a.segment_id  LEFT JOIN gaosu  d  ON c.road_id=d.road_id  LEFT JOIN event_images_info e ON e.event_id =a.event_id  WHERE 1=1 ";

		if (paramsBean.getCity() != null && !paramsBean.getCity().equals("")) {
			sql2 += " and a.city_name='" + paramsBean.getCity() + "'";
		}

		if (paramsBean.getDataList() != null) {
			if (paramsBean.getIsContinuous().equals("true")) {

				if (paramsBean.getDataList().length > 0) {

					sql2 += " and substring(a.upload_time,0,11) between '" + paramsBean.getDataList()[0] + "' and '"
							+ paramsBean.getDataList()[1] + "'";

				}
			} else {
				String es2 = "(";
				for (int i = 0; i < paramsBean.getDataList().length; i++) {
					es2 += "'" + paramsBean.getDataList()[i] + "',";
				}
				es2 = es2.substring(0, es2.length() - 1) + ")";
				if (paramsBean.getDataList().length > 0) {
					sql2 += " and substring(a.upload_time,0,11) in " + es2;
				}

			}
		}

		if (paramsBean.getEventsList() != null) {
			String es = "(";
			for (int i = 0; i < paramsBean.getEventsList().length; i++) {
				es += "'" + paramsBean.getEventsList()[i] + "',";
			}
			es = es.substring(0, es.length() - 1) + ")";
			if (paramsBean.getEventsList().length > 0) {
				sql2 += " and a.event_type in " + es;
			}

		}

		if (paramsBean.getTimeFrame() != null) {
			if (paramsBean.getTimeFrame().length > 0) {
				sql2 += " and substring(a.upload_time,12,5) between '" + paramsBean.getTimeFrame()[0] + "' and '"
						+ paramsBean.getTimeFrame()[1] + "'";
			}
		}
		if (paramsBean.getRoadSecList() != null) {
			String es1 = "(";
			for (int i = 0; i < paramsBean.getRoadSecList().length; i++) {
				es1 += paramsBean.getRoadSecList()[i] + ",";
			}
			es1 = es1.substring(0, es1.length() - 1) + ")";
			if (paramsBean.getRoadSecList().length > 0) {
				sql2 += " and d.r_id in " + es1;
			}
		}

		sql2 += "ORDER BY a.upload_time  ASC";

		System.err.println("sql2:" + sql2);
		List<String> list = new ArrayList<String>();

		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql2);
		for (Map<String, Object> map : queryForList) {
			for (String s : map.keySet()) {
				if (s.equals("event_id")) {
					String filePath = "http://117.51.149.90/videos/" + map.get(s) + ".mp4";
					list.add(filePath);
				}
			}

		}

		return list;
	}

	public Object getVideo(ParamsBean paramsBean) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		if (paramsBean.getEventId() != null && !paramsBean.getEventId().equals("")) {
			String filePath = "http://117.51.149.90/videos/" + paramsBean.getEventId() + ".mp4";
			URL url = new URL(filePath);
			HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
			String message = urlcon.getHeaderField(0);
			if (StringUtils.hasText(message) && message.startsWith("HTTP/1.1 404")) {
				map.put("videoPath", "");
			} else {
				map.put("videoPath", filePath);
			}
		}

		JSONObject json = new JSONObject(map);
		return json;
	}

}
