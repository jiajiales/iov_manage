package com.cennavi.audi_data_collect.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cennavi.audi_data_collect.service.EventHeatService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Created by cennavi on 2019/4/25.
 */
@RestController
@RequestMapping("/event")
public class EventHeatController {
	
	private static double EARTH_RADIUS = 6381.372;
	
    @Autowired
    private EventHeatService eventHeatService;

    BigDecimal roadLength = new BigDecimal(0);
    List<Coordinate> clist = null;
    List<LineString> slist = null;
    
    /**
     * 处理高速数据.
     */
    @ResponseBody
    @RequestMapping("/dealData")
    public void dealData(){
        try {
            List<Map<String, Object>> resultList = eventHeatService.getGaoSuLines();
            String geomStr = "";
            Geometry geom;
            Coordinate[] cs;
            Coordinate[] newcs1;
            GeometryFactory geometryFactory = new GeometryFactory();
            String road_name;
            int road_id;
            for(int i=0; i<resultList.size(); i++) {
            	road_name = resultList.get(i).get("name").toString();
            	road_id = Integer.parseInt(resultList.get(i).get("road_id").toString());
            	clist = new ArrayList<Coordinate>();
            	slist = new ArrayList<LineString>();
            	geomStr = resultList.get(i).get("wkt").toString();
            	geom = new WKTReader().read(geomStr);
            	cs = geom.getCoordinates();
            	
            	for(int j=0;j<(cs.length-1);j++) {
            		saveSegment(cs[j],cs[j+1]);
				}
            	
            	clist.add(cs[cs.length-1]);
            	newcs1 = new Coordinate[clist.size()];
    			for(int n=0;n<clist.size();n++) {
    				newcs1[n] = clist.get(n);
    			}
    			
    			slist.add(geometryFactory.createLineString(newcs1));
            	
            	for(int m=0;m<slist.size();m++) {
            		Map<String, Object> map =  new HashMap<String, Object>();
            		map.put("road_name", road_name);
            		map.put("road_id", road_id);
            		map.put("geom", slist.get(m));
            		eventHeatService.insertSegment(map);
            	}
            	
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void saveSegment(Coordinate c1,Coordinate c2) {
    	BigDecimal distance,t_distance;
        BigDecimal p1_x,p1_y,p2_x,p2_y;
        BigDecimal np_x,np_y;
        BigDecimal length = new BigDecimal(200);
        
        Coordinate[] newcs;
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point1;
        Point point2;
        
        point1 = geometryFactory.createPoint(c1);
		point2 = geometryFactory.createPoint(c2);
		clist.add(c1);
		System.out.println("point1="+point1.toText());
		System.out.println("point2="+point2.toText());
		distance = new BigDecimal(point1.distance(point2));
		System.out.println("distance==="+distance);
		roadLength = roadLength.add(distance);
		System.out.println("roadLength==="+roadLength);
		LineString segment = null;
		Coordinate nc;
		if(roadLength.compareTo(length) >= 0){
			//超过200米
			t_distance = length.subtract(roadLength.subtract(distance));
			p1_x = new BigDecimal(point1.getX());
			p1_y = new BigDecimal(point1.getY());
			p2_x = new BigDecimal(point2.getX());
			p2_y = new BigDecimal(point2.getY());
			np_x = t_distance.multiply(p2_x.subtract(p1_x).divide(distance, 2, BigDecimal.ROUND_HALF_UP)).add(p1_x).setScale(7, BigDecimal.ROUND_HALF_UP);
			np_y = t_distance.multiply(p2_y.subtract(p1_y).divide(distance, 2, BigDecimal.ROUND_HALF_UP)).add(p1_y).setScale(7, BigDecimal.ROUND_HALF_UP);
			System.out.println("new point x = " + np_x + ";y = " + np_y);
			
			newcs = new Coordinate[clist.size()+1];
			for(int k=0;k<clist.size();k++) {
				newcs[k] = clist.get(k);
			}
			nc = new Coordinate(np_x.doubleValue(), np_y.doubleValue());
			newcs[newcs.length-1] = nc;
			segment = geometryFactory.createLineString(newcs);
			
			slist.add(segment);
			
			roadLength = new BigDecimal(0);
			clist = new ArrayList<Coordinate>();
			
			
			saveSegment(nc, c2);
		}
		
    }
    
    private static double rad(double d) {
		return d * Math.PI / 180.0;
	}
    
    /**
	 * 通过经纬度获取距离(单位：米)
	 * 
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return 距离
	 */
	private double getDistance(double lng1, double lat1, double lng2,
			double lat2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000d) / 10000d;
		s = s * 1000;
		return s;
	}
	
	public static void t1(int i) {
		System.out.println("t1------"+(i--));
		
		if(i>0)t1(i);
		System.out.println("==========t1");
	}
	
	public static void main(String[] args) {
		t1(5);
	}

}
