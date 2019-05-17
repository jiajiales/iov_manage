package com.cennavi.audi_data_collect.bean;

public class EventPV {
private  String id;
private  Integer eventId;
private String description;
private String comment;
private String uploadTime;

public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getUploadTime() {
	return uploadTime;
}
public void setUploadTime(String uploadTime) {
	this.uploadTime = uploadTime;
}

public Integer getEventId() {
	return eventId;
}
public void setEventId(Integer eventId) {
	this.eventId = eventId;
}
public String getDescription() {
	return description;
}
public void setDescription(String description) {
	this.description = description;
}
public String getComment() {
	return comment;
}
public void setComment(String comment) {
	this.comment = comment;
}


}
