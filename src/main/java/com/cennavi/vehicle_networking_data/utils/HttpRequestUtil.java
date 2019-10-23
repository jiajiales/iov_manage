package com.cennavi.vehicle_networking_data.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class HttpRequestUtil {

	private static Logger log = LoggerFactory.getLogger(HttpRequestUtil.class);
	/**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            log.info("request-url: " + urlNameString);
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            //for (String key : map.keySet()) {
            //    System.out.println(key + "--->" + map.get(key));
            //}
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
        	log.error("send-get-error", e);
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }  
    
    public static void main(String[] args) {
        //发送 GET 请求
//        String text=HttpRequestUtil.sendGet("http://testfordcs.trafficeye.com.cn/TrafficLocation/locationdata.json", "lon=120.746821&lat=31.3562594");
//        //System.out.println(text);
//        JSONObject obj = JSONObject.fromObject(text);
//        String success = obj.getString("success");
//        JSONObject obj2 = obj.getJSONObject("content");
//        String address = obj2.getString("address");
//        
//        System.out.println(success + ", " + address);
        
//        //发送 POST 请求
//        String params = "[" + 
//        		"	{         " + 
//        		"		\"longitude\": 1191229883," + 
//        		"		\"latitude\": 399211388     " + 
//        		"	},     " + 
//        		"	{         " + 
//        		"		\"longitude\": 1191092449,         " + 
//        		"		\"latitude\": 399196963     " + 
//        		"		" + 
//        		"	}," + 
//        		"	{         " + 
//        		"		\"longitude\": 1190984667,         " + 
//        		"		\"latitude\": 399200478     " + 
//        		"		" + 
//        		"	}," + 
//        		"	{         " + 
//        		"		\"longitude\": 1190877068,         " + 
//        		"		\"latitude\": 399211742     " + 
//        		"		" + 
//        		"	}," + 
//        		"	{         " + 
//        		"		\"longitude\": 1190714510,         " + 
//        		"		\"latitude\": 399220070     " + 
//        		"		" + 
//        		"	}" + 
//        		"]";
    	
    	String params = "[{" + 
    			"	\"deviceID\": \"aaaaaaaaa\"," + 
    			"	\"gpstime\": 1521770416," + 
    			"	\"longitude\": 1234400100," + 
    			"	\"latitude\": 418053300" + 
    			"}, {" + 
    			"	\"deviceID\": \"aaaaaaaaa\"," + 
    			"	\"gpstime\": 1521770419," + 
    			"	\"longitude\": 1234397500," + 
    			"	\"latitude\": 418053100" + 
    			"}, {" + 
    			"	\"deviceID\": \"aaaaaaaaa\"," + 
    			"	\"gpstime\": 1521770422," + 
    			"	\"longitude\": 1234394400," + 
    			"	\"latitude\": 418052800" + 
    			"}, {" + 
    			"	\"deviceID\": \"aaaaaaaaa\"," + 
    			"	\"gpstime\": 1521770425," + 
    			"	\"longitude\": 1234391300," + 
    			"	\"latitude\": 418052599" + 
    			"}, {" + 
    			"	\"deviceID\": \"aaaaaaaaa\"," + 
    			"	\"gpstime\": 1521770428," + 
    			"	\"longitude\": 1234388000," + 
    			"	\"latitude\": 418052100" + 
    			"}, {" + 
    			"	\"deviceID\": \"aaaaaaaaa\"," + 
    			"	\"gpstime\": 1521770431," + 
    			"	\"longitude\": 1234385000," + 
    			"	\"latitude\": 418052000" + 
    			"}]";
        String sr = HttpRequestUtil.sendPost("http://mineservice.minedata.cn/service/lbs/service/search/multi-trajectory?appKey=3a127c8980e445a08013b08c33ef37a6", params);
        System.out.println(sr);
    }
}
