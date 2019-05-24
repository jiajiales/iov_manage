package com.cennavi.audi_data_collect.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cennavi.audi_data_collect.bean.CVSBean;
import com.cennavi.audi_data_collect.bean.EventPV;
import com.cennavi.audi_data_collect.bean.ExcelData;
import com.cennavi.audi_data_collect.bean.ParamsBean;
import com.cennavi.audi_data_collect.bean.User;
import com.cennavi.audi_data_collect.service.EventUserService;
import com.cennavi.audi_data_collect.util.CSVUtil;
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
			
			//图片列表
			@RequestMapping(value = "/findImages")
			public Object findImages(@RequestBody ParamsBean paramsBean) throws Exception{
				return eventUserService.findImages(paramsBean);
			}
			//获取视频地址
			@RequestMapping(value = "/getVideo")
			public Object getVideo(@RequestBody ParamsBean paramsBean) throws Exception{
				return eventUserService.getVideo(paramsBean);
			}
			
			//编辑图片描述
			@RequestMapping(value = "/editImageDescription")
			public Object editImageDescription(@RequestBody EventPV eventPV) throws Exception{
				return eventUserService.editImageDescription(eventPV);
			}
			
			//添加视频评论
			@RequestMapping(value = "/addVideoComment")
			public Object addVideoComment(@RequestBody EventPV eventPV) throws Exception{
				return eventUserService.addVideoComment(eventPV);
			}
			
			//视频评论列表
			@RequestMapping(value = "/findVideoCommentList")
			public Object findVideoCommentList(@RequestBody EventPV eventPV) throws Exception{
				return eventUserService.findVideoCommentList(eventPV);
			}
			
            @RequestMapping(value = "/exportCsv")
					public Object exportCsv(HttpServletResponse response) throws ParseException   {
            	      
					  List<EventPV> list=	eventUserService.exportCsv();
				        HashMap map = new LinkedHashMap();
				        map.put("1", "id");
				        map.put("2", "event_id");
				        map.put("3", "comment");
				        map.put("4", "upload_time");
				        String fileds[] = new String[] { "id","eventId", "comment","uploadTime"};
				        try {
							CSVUtil.exportFile(response, map, list, fileds);
							/*
							 * response：直接传入response
							 * map：对应文件的第一行 
							 * list：对应 List<CVSBean>  list对象形式
							 * fileds：对应每一列的数据
							 * */
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}//直接调用

		            
			        return null;
			    }
            
            @RequestMapping(value = "/downloadImages")
            public void downloadImages(@RequestBody ParamsBean paramsBean,HttpServletRequest request, HttpServletResponse response) throws IOException{
            	List<String> list=eventUserService.findImagesUrl(paramsBean);
                try {
                	   String downloadFilename = "Export_images.zip";//文件的名称
                    downloadFilename = URLEncoder.encode(downloadFilename, "UTF-8");//转换中文否则可能会产生乱码
                    response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
                    response.setHeader("Content-Disposition", "attachment;filename=" + downloadFilename);// 设置在下载框默认显示的文件名
                    ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
                    String[] files = new String[]{"http://117.51.149.90/images/1137.png","http://117.51.149.90/images/1138.png"};
                    for (String str : list) {
                  	  URL url = new URL(str);
                        String dell = "http://117.51.149.90/images/";
                      String fileName  =str.replace(dell,"");
//                      URL serverUrl = new URL("http://localhost:8090/Demo/clean.sql");
                      HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
                      String message = urlcon.getHeaderField(0);
                      //判断远程服务器是不是有这个文件  如果有就下载,没有就continue
                      if (StringUtils.hasText(message) && message.startsWith("HTTP/1.1 404")) {
                        continue;
                      }else{
                    	  zos.putNextEntry(new ZipEntry(fileName));
                          //FileInputStream fis = new FileInputStream(new File(files[i])); 
                          InputStream fis = url.openConnection().getInputStream();  
                          byte[] buffer = new byte[1024];    
                          int r = 0;    
                          while ((r = fis.read(buffer)) != -1) {    
                              zos.write(buffer, 0, r);    
                          }    
                          fis.close();  
                      }
                      
                      
                    }
                    zos.flush();    
                    zos.close();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 
        }
            
            
            @RequestMapping(value = "/downloadVideos")
            public void downloadVideos(@RequestBody ParamsBean paramsBean,HttpServletRequest request, HttpServletResponse response) throws IOException{
            	List<String> list=eventUserService.findVideosUrl(paramsBean);
                try {
                	   String downloadFilename = "Export_videos.zip";//文件的名称
                    downloadFilename = URLEncoder.encode(downloadFilename, "UTF-8");//转换中文否则可能会产生乱码
                    response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
                    response.setHeader("Content-Disposition", "attachment;filename=" + downloadFilename);// 设置在下载框默认显示的文件名
                    ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
                    String[] files = new String[]{"http://117.51.149.90/images/1137.png","http://117.51.149.90/images/1138.png"};
                    for (String str : list) {
                  	  URL url = new URL(str);
                        String dell = "http://117.51.149.90/videos/";
                      String fileName  =str.replace(dell,"");
//                      URL serverUrl = new URL("http://localhost:8090/Demo/clean.sql");
                      HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
                      String message = urlcon.getHeaderField(0);
                      //判断远程服务器是不是有这个文件  如果有就下载,没有就continue
                      if (StringUtils.hasText(message) && message.startsWith("HTTP/1.1 404")) {
                        continue;
                      }else{
                    	  zos.putNextEntry(new ZipEntry(fileName));
                          //FileInputStream fis = new FileInputStream(new File(files[i])); 
                          InputStream fis = url.openConnection().getInputStream();  
                          byte[] buffer = new byte[1024];    
                          int r = 0;    
                          while ((r = fis.read(buffer)) != -1) {    
                              zos.write(buffer, 0, r);    
                          }    
                          fis.close();  
                      }
                      
                      
                    }
                    zos.flush();    
                    zos.close();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 
        }
            
            @RequestMapping(value = "/download")
            public void download(HttpServletRequest request, HttpServletResponse response){
            	 
                try {
                    String downloadFilename = "中文.zip";//文件的名称
                    downloadFilename = URLEncoder.encode(downloadFilename, "UTF-8");//转换中文否则可能会产生乱码
                    response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
                    response.setHeader("Content-Disposition", "attachment;filename=" + downloadFilename);// 设置在下载框默认显示的文件名
                    ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
                    String[] files = new String[]{"http://117.51.149.90/videos/1144.mp4","http://117.51.149.90/videos/1207.mp4"};
                    for (int i=0;i<files.length;i++) {
                        URL url = new URL(files[i]);
                       zos.putNextEntry(new ZipEntry(i+".mp4"));
                       //FileInputStream fis = new FileInputStream(new File(files[i])); 
                       InputStream fis = url.openConnection().getInputStream();  
                       byte[] buffer = new byte[1024];    
                       int r = 0;    
                       while ((r = fis.read(buffer)) != -1) {    
                           zos.write(buffer, 0, r);    
                       }    
                       fis.close();  
                      } 
                    zos.flush();    
                    zos.close();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 
        }
            
         
    		
            @ResponseBody
			@RequestMapping(value = "/exportExcell")
				public Object exportExcell(@RequestBody ParamsBean paramsBean,HttpServletResponse response) throws Exception {
				
				ExcelData data = new ExcelData();
	            data.setName("Event_Data");
	            List<String> titles = new ArrayList<String>();
	            titles.add("event_id");
	            titles.add("type_name");
	            titles.add("road_name");
	            titles.add("date");
	            titles.add("time");
	            titles.add("comment");
	            data.setTitles(titles);

	            List<List<Object>> rows = new ArrayList<List<Object>>();
				  List<CVSBean> list=	eventUserService.exportExcell(paramsBean);
				  
				  for (int i = 0; i < list.size(); i++) {//遍历数组，把数组内容放进Excel的行中
		                List<Object> row = new ArrayList<Object>();
		                row.add(list.get(i).getEvent_id());
		                row.add(list.get(i).getType_name());
		                row.add(list.get(i).getRoad_name());
		                row.add(list.get(i).getDate());
		                row.add(list.get(i).getTime());
		                row.add(list.get(i).getComments());
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
}
