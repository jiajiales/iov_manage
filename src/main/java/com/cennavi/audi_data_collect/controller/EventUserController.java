package com.cennavi.audi_data_collect.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cennavi.audi_data_collect.bean.CVSBean;
import com.cennavi.audi_data_collect.bean.ExcelData;
import com.cennavi.audi_data_collect.bean.ParamsBean;
import com.cennavi.audi_data_collect.bean.User;
import com.cennavi.audi_data_collect.service.EventUserService;
import com.cennavi.audi_data_collect.util.ExcelOutPutUtils;

@RestController
@RequestMapping("/eventUser")
public class EventUserController {
	
	 @Autowired
	    private EventUserService eventUserService;
	 
	//验证用户信息
	@RequestMapping(value = "/checkUser")
	public boolean checkUser(@RequestBody User user){
//		  JSONObject  json = JSONObject.parseObject(JsonDate);
		return	eventUserService.check(user.getName(),user.getPassword());
	}
	
	 //类型列表
	@RequestMapping(value = "/queryEventType")
	public Object queryEventType(){
		return	eventUserService.queryEventType();
	}
	
	
	//直方图
		@RequestMapping(value = "/queryHistogram")
		public Object queryHistogram(@RequestBody  String queryHistogramDate){
			
			String startTmie="",endTime="",startTimeFrames="",endTimeFrames="", dataLists="", eventsList="";
			  JSONObject  json = JSONObject.parseObject(queryHistogramDate);
			  if(json.getString("isContinuous").equals("true")) {
				   JSONArray str = json.getJSONArray("dataList");
				   Map<Integer, String> map=new HashMap<Integer,String>();
				   int i=0;
				   for (Object obj : str) {
					   map.put(i, obj.toString());
					   i++;
				}
				   if(!map.get(0).equals("") && map.get(0)!=null) {
					   startTmie=map.get(0);
				   }
				   if(!map.get(1).equals("") && map.get(1)!=null) {
					   endTime=map.get(1);
				   }
				  
			  }else {
				  JSONArray dataList = json.getJSONArray("dataList");
				   String data=null;
				   String endData="";
				   for (Object obj : dataList) {
					   data ="'" +obj + "',";
					   endData += data;
				}
				   if(endData.length()>0) {
					   dataLists= endData.substring(0,endData.length()-1);
				   }
				  
			  }
			   
			   JSONArray timeFrames = json.getJSONArray("timeFrame");
			   Map<Integer, String> map2=new HashMap<Integer,String>();
			   int n=0;
			   for (Object obj : timeFrames) {
				   map2.put(n, obj.toString());
				   n++;
				
			}
			   if(!map2.get(0).equals("") && map2.get(0)!=null) {
				   startTimeFrames=map2.get(0);
			   }
			   if(!map2.get(1).equals("") && map2.get(1)!=null) {
				   endTimeFrames=map2.get(1);
			   }
			  String k2=null;
			   String endstr2="";
			  JSONArray str3 = json.getJSONArray("roadSecList");
			  for (Object obj : str3) {
				    k2 =obj + ",";
				   endstr2 += k2;
			}
			  String  roadSecList="";
			  if(endstr2.length()>0) {
				    roadSecList= endstr2.substring(0,endstr2.length()-1);
			  }
			  
			  
//			  json.getString("isContinuous");
			return	eventUserService.queryHistogram(json.getString("city"),json.getString("eventType"),startTmie,endTime,roadSecList,startTimeFrames,endTimeFrames,json.getString("isContinuous"),dataLists);
		
		}
		
	
     //数据统计
	@RequestMapping(value = "/dataStatistics")
	public Object dataStatistics(@RequestBody String dataStatisticsDate){
		
		String city="",sort="",isContinuous="",startTmie="",endTime="",startTimeFrames="",endTimeFrames="",  dataLists="", eventsList="";
		  JSONObject  json = JSONObject.parseObject(dataStatisticsDate);
		  if(json.getString("isContinuous").equals("true")) {
			   JSONArray str = json.getJSONArray("dataList");
			   Map<Integer, String> map=new HashMap<Integer,String>();
			   int i=0;
			   for (Object obj : str) {
				   map.put(i, obj.toString());
				   i++;
			}
			   if(map.get(0)!=null && !map.get(0).equals("")  ) {
				   startTmie=map.get(0);
			   }
			   if(  map.get(1)!=null && !map.get(1).equals("")) {
				   endTime=map.get(1);
			   }
			  
		  }else {
			  JSONArray dataList = json.getJSONArray("dataList");
			   String data=null;
			   String endData="";
			   for (Object obj : dataList) {
				   data ="'" +obj + "',";
				   endData += data;
			}
			   if(endData.length()>0) {
				   dataLists= endData.substring(0,endData.length()-1);
			   }
			 
		  }
		
		   
		   JSONArray timeFrames = json.getJSONArray("timeFrame");
		   
		   Map<Integer, String> map2=new HashMap<Integer,String>();
		   int n=0;
		   for (Object obj : timeFrames) {
			   map2.put(n, obj.toString());
			   n++;
			
		}
		   if(map2.get(0)!=null  && !map2.get(0).equals("")  ) {
			   startTimeFrames=map2.get(0);
		   }
		   if( map2.get(1)!=null && !map2.get(1).equals("")) {
			   endTimeFrames=map2.get(1);
		   }
		   JSONArray str2 = json.getJSONArray("eventsList");
		   String kk=null;
		   String endstr="";
		   for (Object obj : str2) {
			   kk ="'" +obj + "',";
			   endstr += kk;
		}
		   if(endstr.length()>0) {
			     eventsList= endstr.substring(0,endstr.length()-1);
		   }
		 
		  String k2=null;
		   String endstr2="";
		  JSONArray str3 = json.getJSONArray("roadSecList");
		  for (Object obj : str3) {
			  k2 =obj + ",";
			   endstr2 += k2;
		}
		  String  roadSecList="";
		  if(endstr2.length()>0) {
			    roadSecList= endstr2.substring(0,endstr2.length()-1);
		  }
		 
		return	eventUserService.dataStatistics(city,eventsList,startTmie,endTime,roadSecList,startTimeFrames,endTimeFrames,sort,isContinuous,dataLists);
				
	}
	
	//折线图
		@RequestMapping(value = "/brokenLine")
		public Object brokenLine(@RequestBody String brokenLineData){
			
			String city="",eventType="",isContinuous="",startTmie="",endTime="",startTimeFrames="",endTimeFrames="",  dataLists="";
			
			  JSONObject  json = JSONObject.parseObject(brokenLineData);
			 
			  if(json.getString("isContinuous").equals("true")) {
				   JSONArray str = json.getJSONArray("dataList");
//				   str.size()>1;
				   Map<Integer, String> map=new HashMap<Integer,String>();
				   int i=0;
				   for (Object obj : str) {
					   map.put(i, obj.toString());
					   i++;
				}
//				  if(map.size()>1) {
//					  
//				  }  
				   if(map.get(0)!=null  &&  !map.get(0).equals("")  ) {
					   startTmie=map.get(0);
				   }
				   if(map.get(1)!=null &&   !map.get(1).equals("") ) {
					   endTime=map.get(1);
				   }
				  
			  }else {
				  JSONArray dataList = json.getJSONArray("dataList");
				   String data=null;
				   String endData="";
				   for (Object obj : dataList) {
					   data ="'" +obj + "',";
					   endData += data;
				}
				   if(endData.length()>0) {
				   dataLists= endData.substring(0,endData.length()-1);
				   }
			  }
			   
			   JSONArray timeFrames = json.getJSONArray("timeFrame");
			   Map<Integer, String> map2=new HashMap<Integer,String>();
			   int n=0;
			   for (Object obj : timeFrames) {
				   map2.put(n, obj.toString());
				   n++;
				
			}
			   if(map2.get(0)!=null && !map2.get(0).equals("")  ) {
				   startTimeFrames=map2.get(0);
			   }
			   if(map2.get(1)!=null && !map2.get(1).equals("")  ) {
				   endTimeFrames=map2.get(1);
			   }
			  String k2=null;
			   String endstr2="";
			  JSONArray str3 = json.getJSONArray("roadSecList");
			  for (Object obj : str3) {
				  k2 =obj + ",";
				   endstr2 += k2;
			}
			  String  roadSecList="";
			  if(endstr2.length()>0) {
				    roadSecList= endstr2.substring(0,endstr2.length()-1);
			  }
			  		  city=json.getString("city");
					  eventType=json.getString("eventType");
					  isContinuous=json.getString("isContinuous");
			return	eventUserService.brokenLine(city,eventType,startTmie,endTime,roadSecList,startTimeFrames,endTimeFrames,isContinuous,dataLists);
		}
	 
		//数据导出
		  @ResponseBody
				@RequestMapping(value = "/exportCsvs")
//					public Object exportCsvs(ParamsBean paramsBean,HttpServletResponse response) throws Exception {
					public Object exportCsvs(@RequestBody ParamsBean paramsBean,HttpServletResponse response) throws Exception {

					
					ExcelData data = new ExcelData();
		            data.setName("Event_Data");
		            List<String> titles = new ArrayList<String>();
		            titles.add("event_id");
		            titles.add("type_name");
		            titles.add("road_name");
		            titles.add("date");
		            titles.add("time");
		            data.setTitles(titles);

		            List<List<Object>> rows = new ArrayList<List<Object>>();
					
					
					  List<CVSBean> list=	eventUserService.exportCsvs(paramsBean);
					  
					  for (int i = 0; i < list.size(); i++) {//遍历数组，把数组内容放进Excel的行中
			                List<Object> row = new ArrayList<Object>();
			                row.add(list.get(i).getEvent_id());
			                row.add(list.get(i).getType_name());
			                row.add(list.get(i).getRoad_name());
			                row.add(list.get(i).getDate());
			                row.add(list.get(i).getTime());
			                rows.add(i, row);
			            }

			            data.setRows(rows);


			            //生成本地
			            /*File f = new File("c:/test.xlsx");
			            FileOutputStream out = new FileOutputStream(f);
			            ExportExcelUtils.exportExcel(data, out);
			            out.close();*/
			            SimpleDateFormat fdate=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			            String fileName="Event_Data.xlsx";//老版本的office改成xls
			            ExcelOutPutUtils.exportExcel(response,fileName,data);
			            
			        return null;
			    }
		  
		  @ResponseBody
			@RequestMapping(value = "/export")
				public Object export(@RequestBody ParamsBean paramsBean,HttpServletResponse response) throws Exception {
System.err.println(paramsBean);
		  return  paramsBean;
		  }
		
}
