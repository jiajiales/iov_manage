package com.cennavi.audi_data_collect.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
		return	eventUserService.check(user.getName(),user.getPassword());
	}
	
	 //类型列表
	@RequestMapping(value = "/queryEventType")
	public Object queryEventType(){
		return	eventUserService.queryEventType();
	}
	
		//数据导出
		  @ResponseBody
				@RequestMapping(value = "/exportCsvs")
					public Object exportCsvs(ParamsBean paramsBean,HttpServletResponse response) throws Exception {
					
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
		  
		//数据统计   
			@RequestMapping(value = "/dataStatistics")
			public Object dataStatistic(@RequestBody ParamsBean paramsBean) throws Exception{
				return eventUserService.dataStatistic(paramsBean);
			}
			
			//折线图   
			@RequestMapping(value = "/brokenLine")
			public Object brokenLines(@RequestBody ParamsBean paramsBean) throws Exception{
				return eventUserService.brokenLines(paramsBean);
			}
//			 
			//柱状图
			@RequestMapping(value = "/queryHistogram")
			public Object queryHistograms(@RequestBody ParamsBean paramsBean) throws Exception{
				return eventUserService.queryHistograms(paramsBean);
			}
}
