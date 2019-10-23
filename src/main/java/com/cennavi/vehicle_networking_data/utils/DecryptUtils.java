package com.cennavi.vehicle_networking_data.utils;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//import com.cennavi.anlian_data_collect.util.HttpUtils;

//import io.lettuce.core.dynamic.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by 谯斌 on 2019/5/31.
 */

public class DecryptUtils {
	
    /*
	 * 压缩数据 (Snappy)
	 */
    public static byte[] compress(byte[] arg0) {
        ByteArrayOutputStream output;
        output = new ByteArrayOutputStream();
        GZIPOutputStream input = null;
        try {
            input = new GZIPOutputStream(output);
            input.write(arg0);
            input.finish();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
        return output.toByteArray();
    }

    /*
      * 解压缩数据 (Snappy)
      */
    public static byte[] uncompress(byte[] arg0) {
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

    public static  Object getFootPoint(String  responseBody){
    	JSONArray jsonArray2 = new JSONArray();
    	JSONArray jsonArray4 = new JSONArray();
    	  if(responseBody!=null && !responseBody.equals("")) {
    		  jsonArray2 = JSONArray.parseArray(responseBody);
    		  if(jsonArray2.size()>0) {
    			  for(int i=0;i<jsonArray2.size();i++){
    			      JSONObject job = jsonArray2.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
    			      // 得到 每个对象中的属性值
    			      JSONArray jsonArray3 = new JSONArray();
    			      if(job.get("matchedList")!=null) {
    			    	    jsonArray3 =(JSONArray) job.get("matchedList");
    			    	    
    	    			      if(jsonArray3.size()>0) {
    	    			    	  JSONObject job2 = jsonArray3.getJSONObject(0);
    	    			    	 String  str= (String) job2.get("footpoint");
    	    			    	   JSONObject node = new JSONObject();
    	    					    node.put("point", str);
    	    					    node.put("index",  i);
    	    					    node.put("match",  1);
    	    					    jsonArray4.add(node);
    	    			      }
    			      }else {
	      			    	JSONObject node = new JSONObject();
	  					    node.put("point", "");
	  					    node.put("index",  i);
	  					    node.put("match",  0);
	  					  jsonArray4.add(node);
	    			      }
    			  
    			    	 
    			  }
    	  }
		
		  }
    	return  jsonArray4;
    }
    
    public static  Object  getArry(String strs,String matchUrl) {
    	Map<String, Object> map=new HashMap<String, Object>();
    	String rJson=null;
		JSONArray jsonArray = new JSONArray();
		 jsonArray = JSONArray.parseArray(strs);
		map.put("trajCount", "63");
		map.put("subParam", jsonArray);
		JSONObject jsonMap = new JSONObject(map);
		try {
			String resonponse=	HttpUtils.sendJsonToPost(matchUrl,jsonMap.toString());
			JSONObject json2=JSONObject.parseObject(resonponse);
		String str=	 json2.get("responseBody").toString();
			 rJson = new String(uncompress(Base64.getDecoder().decode(str.getBytes())));
		} catch (Exception e) {
			e.printStackTrace();
		}
	 
	 if(rJson.length()>4) {
		 return getFootPoint(rJson);
	 }
		return null;
		
		
    }

}
