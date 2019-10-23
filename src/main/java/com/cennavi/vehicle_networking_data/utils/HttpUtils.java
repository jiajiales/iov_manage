package com.cennavi.vehicle_networking_data.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.alibaba.fastjson.JSONObject;

public class HttpUtils {

	/**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url 发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendJsonToPost(String url, String requestParam) throws Exception {
		 String result = "";
		JSONObject resultJsonObject=null;
		HttpPost post = null;
		try {
		HttpClient httpClient = new DefaultHttpClient();
//		String url="http://mineservice.minedata.cn/service/lbs/service/search/multi-trajectory?appKey=3b3bad1077b441f8868d2de8d464fca9";
		post = new HttpPost(url);
		post.setHeader("Content-type", "application/json;charset=utf-8");
		post.setHeader("Connection", "Close");
		StringEntity entity = new StringEntity(requestParam, java.nio.charset.Charset.forName("UTF-8"));
		entity.setContentEncoding("UTF-8");
		// 发送Json格式的数据请求
		entity.setContentType("application/json");
		post.setEntity(entity);
		System.out.println("post===" + post);
		HttpResponse response = httpClient.execute(post);

		// 检验返回码
		int statusCode = response.getStatusLine().getStatusCode();
		System.out.println(statusCode);
		String line=null;
		
		StringBuilder entityStringBuilder=new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"),8*1024);
       while ((line = in.readLine()) != null) {
           result += line;
       }
		 
		resultJsonObject = JSONObject.parseObject(entityStringBuilder.toString());
		if (statusCode != HttpStatus.SC_OK) {
		//LogUtil.info("请求出错: "+statusCode);
		System.out.println("请求出错: " + statusCode);
		} else {
		int retCode = 0;
		String sessendId = "";
		// 返回码中包含retCode及会话Id
		for (Header header : response.getAllHeaders()) {
		if (header.getName().equals("retcode")) {
		retCode = Integer.parseInt(header.getValue());
		}
		if (header.getName().equals("SessionId")) {
		sessendId = header.getValue();
		}
		}
		}
		} catch (Exception e) {
		e.printStackTrace();
		} finally {
		if (post != null) {
		try {
		post.releaseConnection();
		Thread.sleep(500);
		} catch (InterruptedException e) {
		e.printStackTrace();
		}
		}
		
		}
		return result;
		}
    

}
