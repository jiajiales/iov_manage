package com.cennavi.audi_data_collect.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
@Component
public class EventUserDao {
	 @Autowired
	    private JdbcTemplate jdbcTemplate;
	 
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
	
	//直方图
	public Object queryHistogram(String cityName, String eventType, String startTime, String endTime,String segmentIdArr, String startTimeFrames, String endTimeFrames, String isContinuous, String dataLists) {
		
		String sql="SELECT count(*) as mycount,myhour FROM (  SELECT  upload_time , date_part('hour',to_timestamp(upload_time,'yyyy-MM-dd hh24:mi:ss')) as myhour  FROM  collection_info_new a  LEFT JOIN  gaosu_segment  c  ON c.id=a.segment_id  LEFT JOIN gaosu  d  ON c.road_id=d.road_id   WHERE  1=1";
		String sql2="SELECT count(event_id) as num   FROM collection_info_new a LEFT JOIN  gaosu_segment  c  ON c.id=a.segment_id   LEFT JOIN gaosu  d  ON c.road_id=d.road_id WHERE  1=1";
		 
		if (!cityName.equals("") && cityName!=null) {
			  sql += " and a.city_name =  '"+cityName+"' ";
			  sql2 += " and a.city_name =  '"+cityName+"' ";
		  		}
		  if (!eventType.equals("") && eventType!=null) {
			  sql += " and a.event_type =  '"+eventType+"' ";
			  sql2 += " and a.event_type =  '"+eventType+"' ";
		  		}
		 
		  
		  if(isContinuous.equals("true")) {  //判断是否连续
			  if (!startTime.equals("") && startTime!=null  && !endTime.equals("") && endTime!=null ) {
				  
				  sql += " AND to_timestamp(a.upload_time,'yyyy-MM-dd')>='"+startTime+"' and to_timestamp(a.upload_time,'yyyy-MM-dd') <=  '"+endTime+"' ";
				  sql2 += " AND to_timestamp(a.upload_time,'yyyy-MM-dd')>='"+startTime+"' and to_timestamp(a.upload_time,'yyyy-MM-dd') <=  '"+endTime+"' ";

			  }
		  }else {
			  sql +="AND  (to_timestamp(a.upload_time,'yyyy-MM-dd') in ("+dataLists+") )";
			  sql2 +="AND  (to_timestamp(a.upload_time,'yyyy-MM-dd') in ("+dataLists+") )";

		  }
		  if (!startTimeFrames.equals("") && startTimeFrames!=null  && !endTimeFrames.equals("") && endTimeFrames!=null ) {
			  sql  += " and substring(a.upload_time,12,16)>= '"+startTimeFrames+"' and substring(a.upload_time,12,16)<= '"+endTimeFrames+"'";
			  sql2  += " and substring(a.upload_time,12,16)>= '"+startTimeFrames+"' and substring(a.upload_time,12,16)<= '"+endTimeFrames+"'";
		  }
		  if ( !segmentIdArr.equals("") &&segmentIdArr!=null) {
//			  sql += " and a.segment_id IN ( "+segmentIdArr+" ) ";
//			  sql2 += " and a.segment_id IN ( "+segmentIdArr+" ) ";
			  
			  sql += "and d.r_id IN  ( "+segmentIdArr+" ) ";
			  sql2 += "and d.r_id IN  ( "+segmentIdArr+" ) ";
		  		}
		  
		  sql+=") as hour_table GROUP BY  myhour ORDER BY myhour ASC";
		  
		  System.err.println("sql:"+sql);
		  System.err.println("sql2:"+sql2);
		 
		 double k=jdbcTemplate.queryForObject(sql2,double.class);  //获取总数
		 System.out.println("k:"+k);
		 List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
		 List< Map<String,Object>> queryForList2 = new ArrayList<Map<String,Object>>();
		 
		 for (Map<String, Object> map : queryForList) {
			 Map<String, Object> map2=  new HashMap<String, Object>();
			 for (String s : map.keySet()) {
				 map2.put(s, map.get(s));
				 if(s.equals("mycount")) {
					 double a=Integer.parseInt(map.get(s).toString());
					 double n=(double)Math.round(a/k*10000)/100;
					 if(n==100.00) {//如果为100 就转成99.99方便前端展示
						 n=99.99;
					 }
					 map2.put("percentage",  n);   //添加百分比数据
				 }
	            }
			 
			 queryForList2.add(map2);
		}
	 
		 
		 JSONObject json;
		 JSONArray jsonArray = new JSONArray();
		 for (Map<String, Object> map : queryForList2) {//将map转成json格式
			   json =new JSONObject(map);
			   jsonArray.add(json);
		}
		 
		return jsonArray;
	}
	
	public Object dataStatistics(String cityName, String eventTypes, String startTime, String endTime, String segmentIds,String startTimeFrames, String endTimeFrames,String sort, String isContinuous, String dataLists ) {
		String sql="SELECT  t1.type_name,t1.id,COALESCE(t2.nums,0) AS  num    from event_type t1  LEFT JOIN  ( SELECT COALESCE(count(a.event_id), 0) as nums  , b.type_name as eventName  FROM  collection_info_new   a   LEFT JOIN   event_type b    ON a.event_type=b.type_code LEFT JOIN  gaosu_segment  c  ON c.id=a.segment_id LEFT JOIN gaosu  d  ON c.road_id=d.road_id WHERE 1=1";
		
		String sql2="SELECT COALESCE(count(a.event_id), 0) as nums   FROM event_type b    LEFT JOIN   collection_info_new   a   ON a.event_type=b.type_code LEFT JOIN  gaosu_segment  c   ON c.id=a.segment_id  LEFT JOIN gaosu  d  ON c.road_id=d.road_id  WHERE 1=1  ";
		
		if (!cityName.equals("") && cityName!=null) {
				  sql += " and a.city_name =  '"+cityName+"' ";
				  sql2 += " and a.city_name =  '"+cityName+"' ";
				 
			  		}
			  if (!eventTypes.equals("") && eventTypes!=null) {
				  sql += " and a.event_type IN ( "+eventTypes+" )";
				  sql2 += " and a.event_type IN ( "+eventTypes+" )";				
			  		}
			  
			  if(isContinuous.equals("true")) {
				  if (!startTime.equals("") && startTime!=null  && !endTime.equals("") && endTime!=null ) {
					  
					  sql += " AND to_timestamp(a.upload_time,'yyyy-MM-dd')>='"+startTime+"' and to_timestamp(a.upload_time,'yyyy-MM-dd') <=  '"+endTime+"' ";
					  sql2 += " AND to_timestamp(a.upload_time,'yyyy-MM-dd')>='"+startTime+"' and to_timestamp(a.upload_time,'yyyy-MM-dd') <=  '"+endTime+"' ";

				  }
			  }else {
				  sql +="AND  (to_timestamp(a.upload_time,'yyyy-MM-dd') in ("+dataLists+") )";
				  sql2 +="AND  (to_timestamp(a.upload_time,'yyyy-MM-dd') in ("+dataLists+") )";

			  }
			 
			  
			  if (!startTimeFrames.equals("") && startTimeFrames!=null  && !endTimeFrames.equals("") && endTimeFrames!=null ) {
				  sql  += " and substring(a.upload_time,12,16)>= '"+startTimeFrames+"' and substring(a.upload_time,12,16)<= '"+endTimeFrames+"'";
				  sql2  += " and substring(a.upload_time,12,16)>= '"+startTimeFrames+"' and substring(a.upload_time,12,16)<= '"+endTimeFrames+"'";

			  }
			  
			  if ( !segmentIds.equals("") &&segmentIds!=null) {
				  sql += "and d.r_id IN  ( "+segmentIds+" ) ";
				  sql2 += "and d.r_id IN  ( "+segmentIds+" ) ";
				
			  		}
			  sql+=" group  by b.type_name   )  t2  ON t2.eventName=t1.type_name ";
		  
			  if ( !sort.equals("") &&sort!=null) {
				  sql += "  order by  num  "+sort+"";
			  		}
			  System.err.println("sql:"+sql);
			  System.err.println("sql2:"+sql2);
			  double k=jdbcTemplate.queryForObject(sql2,double.class);
				 System.out.println("k:"+k);
				
			  List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
			  List< Map<String,Object>> queryForList2 = new ArrayList<Map<String,Object>>();
			  for (Map<String, Object> map : queryForList) {
				  Map<String, Object> map2=  new HashMap<String, Object>();
					 for (String s : map.keySet()) {
						 map2.put(s, map.get(s));
						 if(s.equals("num")) {
							 double a=Integer.parseInt(map.get(s).toString());
							 double n=(double)Math.round(a/k*10000)/100;
							 if(n==100.00) {
								 n=99.99;
							 }
							 map2.put("percentage",  n);
						 }
			            }
					 
					 queryForList2.add(map2);
			}
				JSONObject json;
				 JSONArray jsonArray = new JSONArray();
			  for (Map<String, Object> map : queryForList2) {  
				  json =new JSONObject(map);
				  jsonArray.add(json);
			}
			  	
		return jsonArray;
		
	}
	//折线图
	public Object brokenLine(String cityName, String eventType, String startTime, String endTime, String segmentIds, String startTimeFrames, String endTimeFrames,String isContinuous,String dataLists) {
		String sql="SELECT count(*) as mycount,myhour,road_name FROM (   SELECT  a.upload_time ,b.road_name, date_part('hour',to_timestamp(a.upload_time,'yyyy-MM-dd hh24:mi:ss')) as myhour   FROM  collection_info_new a   left join gaosu_segment b  on a.segment_id=b.id  LEFT JOIN gaosu  d  ON b.road_id=d.road_id where 1=1";
		String sql2="SELECT count(event_id) as num   FROM collection_info_new a  LEFT JOIN  gaosu_segment  c  ON c.id=a.segment_id   LEFT JOIN gaosu  d  ON c.road_id=d.road_id   WHERE 1=1";
		if (!cityName.equals("") && cityName!=null) {
			  sql += " and a.city_name =  '"+cityName+"' ";
			  sql2 += " and a.city_name =  '"+cityName+"' ";
		  		}
		  if (!eventType.equals("") && eventType!=null) {
			  sql += " and a.event_type =  '"+eventType+"' ";
			  sql2 += " and  a.event_type =  '"+eventType+"' ";
		  		}
		  
		  if(isContinuous.equals("true")) {  //判断时间是否连续
			  if (!startTime.equals("") && startTime!=null  && !endTime.equals("") && endTime!=null ) {
				  
				  sql += " AND to_timestamp(a.upload_time,'yyyy-MM-dd')>='"+startTime+"' and to_timestamp(a.upload_time,'yyyy-MM-dd') <=  '"+endTime+"' ";
				  sql2 += " AND to_timestamp(a.upload_time,'yyyy-MM-dd')>='"+startTime+"' and to_timestamp(a.upload_time,'yyyy-MM-dd') <=  '"+endTime+"' ";

			  }
		  }else {
			  sql +="AND  (to_timestamp(a.upload_time,'yyyy-MM-dd') in ("+dataLists+") )";
			  sql2 +="AND  (to_timestamp(a.upload_time,'yyyy-MM-dd') in ("+dataLists+") )";

		  }
		  
		  
		  if (!startTimeFrames.equals("") && startTimeFrames!=null  && !endTimeFrames.equals("") && endTimeFrames!=null ) {
			  sql  += " and substring(a.upload_time,12,16)>= '"+startTimeFrames+"' and substring(a.upload_time,12,16)<= '"+endTimeFrames+"'";
			  sql2  += " and substring(a.upload_time,12,16)>= '"+startTimeFrames+"' and substring(a.upload_time,12,16)<= '"+endTimeFrames+"'";
		  }
		 
		  if ( !segmentIds.equals("") &&segmentIds!=null) {
			  sql += "and d.r_id IN  ( "+segmentIds+" ) ";
			  sql2 += "and d.r_id IN  ( "+segmentIds+" ) ";
//			  sql += " and a.segment_id IN ( "+segmentIds+" ) ";
//			  sql2 += " and  segment_id IN ("+segmentIds+" )";
		  		}
		  
		  sql+=") as hour_table GROUP BY  myhour,road_name   ORDER BY myhour ASC";
		  System.err.println("sql:"+sql);
		  System.err.println("sql2:"+sql2);
			 double k=jdbcTemplate.queryForObject(sql2,double.class);
			 System.out.println("k:"+k);
			 List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
			 List< Map<String,Object>> queryForList2 = new ArrayList<Map<String,Object>>();
			
			 for (Map<String, Object> map : queryForList) {
				 Map<String, Object> map2=  new HashMap<String, Object>();
				 for (String s : map.keySet()) {
					 map2.put(s, map.get(s));
					 if(s.equals("mycount")) {
						 double a=Integer.parseInt(map.get(s).toString());
						 double n=(double)Math.round(a/k*10000)/100;
						 if(n==100.00) {
							 n=99.99;
						 }
						 map2.put("percentage",n);
					 }
		            }
				 
				 queryForList2.add(map2);
			}
			 JSONObject json;
			 JSONArray jsonArray = new JSONArray();
		  for (Map<String, Object> map : queryForList2) {   //将数据转换成json格式
			  json =new JSONObject(map);
			   jsonArray.add(json);
		}
		  
	return jsonArray;
	}

}
