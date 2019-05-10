package com.cennavi.audi_data_collect.dao;

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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cennavi.audi_data_collect.bean.CVSBean;
import com.cennavi.audi_data_collect.bean.ParamsBean;
@Component
public class EventUserDao {
	 @Autowired
	    private JdbcTemplate jdbcTemplate;
	 private static SimpleDateFormat inSDF = new SimpleDateFormat("yyyy-mm-dd");
	  private static SimpleDateFormat outSDF = new SimpleDateFormat("mm/dd/yyyy");
	  private static SimpleDateFormat inSDFH = new SimpleDateFormat("hh:mm:ss");
	  private static SimpleDateFormat outSDFH = new SimpleDateFormat("hh/mm/ss");
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
			  sql  += " and substring(a.upload_time,12,5)>= '"+startTimeFrames+"' and substring(a.upload_time,12,5)<= '"+endTimeFrames+"'";
			  sql2  += " and substring(a.upload_time,12,5)>= '"+startTimeFrames+"' and substring(a.upload_time,12,5)<= '"+endTimeFrames+"'";
		  }
		  if ( !segmentIdArr.equals("") &&segmentIdArr!=null) {
			  
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
		 for (Map<String, Object> map : queryForList2) {//将map转成json格式
			   json =new JSONObject(map);
			   jsonArray.add(json);
		}
		 
		return jsonArray;
	}
	
	public Object dataStatistics(String cityName, String eventTypes, String startTime, String endTime, String segmentIds,String startTimeFrames, String endTimeFrames,String sort, String isContinuous, String dataLists ) {
		String sql="SELECT  t1.event_name_en as typeName ,t1.type_code as typeCode,COALESCE(t2.nums,0) AS  num    from event_type t1  LEFT JOIN  ( SELECT COALESCE(count(a.event_id), 0) as nums  , b.type_name as eventName  FROM  collection_info_new   a   LEFT JOIN   event_type b    ON a.event_type=b.type_code LEFT JOIN  gaosu_segment  c  ON c.id=a.segment_id LEFT JOIN gaosu  d  ON c.road_id=d.road_id WHERE 1=1";
		
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
				  sql  += " and substring(a.upload_time,12,5)>= '"+startTimeFrames+"' and substring(a.upload_time,12,5)<= '"+endTimeFrames+"'";
				  sql2  += " and substring(a.upload_time,12,5)>= '"+startTimeFrames+"' and substring(a.upload_time,12,5)<= '"+endTimeFrames+"'";

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
		String sql="SELECT count(*) as mycount,myhour,road_name,road_id FROM (   SELECT  a.upload_time ,d.en_name as road_name,d.r_id as road_id, date_part('hour',to_timestamp(a.upload_time,'yyyy-MM-dd hh24:mi:ss')) as myhour   FROM  collection_info_new a   left join gaosu_segment b  on a.segment_id=b.id  LEFT JOIN gaosu  d  ON b.road_id=d.road_id where 1=1";
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
			  sql  += " and substring(a.upload_time,12,5)>= '"+startTimeFrames+"' and substring(a.upload_time,12,5)<= '"+endTimeFrames+"'";
			  sql2  += " and substring(a.upload_time,12,5)>= '"+startTimeFrames+"' and substring(a.upload_time,12,5)<= '"+endTimeFrames+"'";
		  }
		 
		  if ( !segmentIds.equals("") &&segmentIds!=null) {
			  sql += "and d.r_id IN  ( "+segmentIds+" ) ";
			  sql2 += "and d.r_id IN  ( "+segmentIds+" ) ";
		  		}
		  
		  sql+=") as hour_table GROUP BY  myhour,road_name,road_id   ORDER BY myhour ASC";
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
	 
	public List<CVSBean> exportCsvs(ParamsBean paramsBean) throws ParseException {
		String sql="SELECT  a.event_id,COALESCE(b.type_name, '0') as type_name,d.name as road_name,substring(a.upload_time,1,10) AS date, substring(a.upload_time,12,7) AS time    FROM collection_info_new a   LEFT JOIN event_type b ON b.type_code=a.event_type LEFT JOIN  gaosu_segment  c  ON c.id=a.segment_id    LEFT JOIN gaosu  d  ON c.road_id=d.road_id   WHERE 1=1  ";
		System.err.println("city:"+paramsBean.getCity());
		if (!paramsBean.getCity().equals("") && paramsBean.getCity()!=null) {
				  sql += " and a.city_name =  '"+paramsBean.getCity()+"' ";
				 
			  		}
			  if (paramsBean.getEventsList().length>0 && paramsBean.getEventsList()!=null) {
				  String eventsList="";
				  for (int i=0;i<paramsBean.getEventsList().length;i++) {
					  eventsList=eventsList+"'"+paramsBean.getEventsList()[i]+"',";
				}
				  if(eventsList.length()>0) {
					  eventsList= eventsList.substring(0,eventsList.length()-1);
				  }
				  sql += " and a.event_type IN ( "+eventsList+" )";
			  		}
			  
			  if(paramsBean.getIsContinuous().equals("true")) {
				  if(paramsBean.getDataList().length>0) {
					  if (!paramsBean.getDataList()[0].equals("") && paramsBean.getDataList()[0]!=null  && !paramsBean.getDataList()[1].equals("") && paramsBean.getDataList()[1]!=null ) {
						  sql += " AND to_timestamp(a.upload_time,'yyyy-MM-dd')>='"+paramsBean.getDataList()[0]+"' and to_timestamp(a.upload_time,'yyyy-MM-dd') <=  '"+paramsBean.getDataList()[1]+"' ";
					  }
				  }
				  
			  }else {
				  String dataLists="";
				  for (int i=0;i<paramsBean.getDataList().length;i++) {
					dataLists=dataLists+"'"+paramsBean.getDataList()[i]+"',";
				}
				  if(dataLists.length()>0) {
				  dataLists= dataLists.substring(0,dataLists.length()-1);
				  }
				  sql +="AND  (to_timestamp(a.upload_time,'yyyy-MM-dd') in ("+dataLists+") )";
			  }
			  if(paramsBean.getTimeFrame().length>0) {
			  
			  if (!paramsBean.getTimeFrame()[0].equals("") && paramsBean.getTimeFrame()[0]!=null  && !paramsBean.getTimeFrame()[1].equals("") && paramsBean.getTimeFrame()[1]!=null ) {
				  sql  += " and substring(a.upload_time,12,5)>= '"+paramsBean.getTimeFrame()[0]+"' and substring(a.upload_time,12,5)<= '"+paramsBean.getTimeFrame()[1]+"'";

			  }}
			  
			  if ( paramsBean.getRoadSecList().length>0 && paramsBean.getRoadSecList()!=null) {
				  
				  String roadSecList="";
				  for (int i=0;i<paramsBean.getRoadSecList().length;i++) {
					  roadSecList=roadSecList+"'"+paramsBean.getRoadSecList()[i]+"',";
				}
				  if(roadSecList.length()>0) {
					  roadSecList= roadSecList.substring(0,roadSecList.length()-1);
				  }
				  sql += "and d.r_id IN  ( "+roadSecList+" ) ";
			  		}
		  System.err.println("sql:"+sql);
		  List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
			List<CVSBean> list =new ArrayList() ;
		  for (Map<String, Object> map : queryForList) {   //将数据转换成json格式
			  CVSBean cVSBean =new CVSBean();
			  for (String k : map.keySet())
		      {
				   if(k.equals("event_id")){
					   cVSBean.setEvent_id((Integer) map.get(k));
				   }   
				   if(k.equals("type_name")  ){
					   cVSBean.setType_name(map.get(k).toString());
				   }   
				   if(k.equals("road_name")){
					   cVSBean.setRoad_name(map.get(k).toString());
				   }   
				   if(k.equals("date")){  //日期格式转换
					   String outDate = "";
					    Date date = inSDF.parse(map.get(k).toString());
			            outDate = outSDF.format(date);
			            cVSBean.setDate(outDate);
				   }   
				   if(k.equals("time")){//时间格式转换
					   String outHour = "";
					    Date date = inSDFH.parse(map.get(k).toString());
			            outHour = outSDFH.format(date);
			            cVSBean.setTime(outHour);
				   }   
	  
		      }
			  list.add(cVSBean);
			   
		}
			return list;
	}

}
