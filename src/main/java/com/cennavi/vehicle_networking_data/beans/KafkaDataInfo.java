package com.cennavi.vehicle_networking_data.beans;

public class KafkaDataInfo {
	private String startTime;//开始时间
	private String endTime;//结束时间
	private String topic;//topic值
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
}
