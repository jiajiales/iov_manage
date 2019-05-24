package com.cennavi.audi_data_collect.bean;

import java.util.List;

public class CVSBean {
private  Integer event_id;
private String type_name;
private String road_name;
private String date;
private String time;
private  String comments;

public String getComments() {
	return comments;
}
public void setComments(String comments) {
	this.comments = comments;
}
public Integer getEvent_id() {
	return event_id;
}
public void setEvent_id(Integer event_id) {
	this.event_id = event_id;
}
public String getType_name() {
	return type_name;
}
public void setType_name(String type_name) {
	this.type_name = type_name;
}
public String getRoad_name() {
	return road_name;
}
public void setRoad_name(String road_name) {
	this.road_name = road_name;
}
public String getDate() {
	return date;
}
public void setDate(String date) {
	this.date = date;
}
public String getTime() {
	return time;
}
public void setTime(String time) {
	this.time = time;
}
 
 
}
