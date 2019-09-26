package com.cennavi.vehicle_networking_data.beans;

public class CarGroupInfo {
	private Integer id;//开始时间
	private Integer bgroupId;//业务组ID
	private String name;//车组名称
	private String workArea;//工作区域
	private String updateTime;//更新时间
	private String limit;//返回数量
	private String offset;//开始点
	private String dataSort;//时间排序
	private String nameSort;//名称排序
	private String bgroupSort;//业务排序
	private String bgroupname;//业务组名称
	
	
	
	
	public String getBgroupname() {
		return bgroupname;
	}
	public void setBgroupname(String bgroupname) {
		this.bgroupname = bgroupname;
	}
	public String getDataSort() {
		return dataSort;
	}
	public void setDataSort(String dataSort) {
		this.dataSort = dataSort;
	}
	public String getNameSort() {
		return nameSort;
	}
	public void setNameSort(String nameSort) {
		this.nameSort = nameSort;
	}
	public String getBgroupSort() {
		return bgroupSort;
	}
	public void setBgroupSort(String bgroupSort) {
		this.bgroupSort = bgroupSort;
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	public String getOffset() {
		return offset;
	}
	public void setOffset(String offset) {
		this.offset = offset;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getBgroupId() {
		return bgroupId;
	}
	public void setBgroupId(Integer bgroupId) {
		this.bgroupId = bgroupId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWorkArea() {
		return workArea;
	}
	public void setWorkArea(String workArea) {
		this.workArea = workArea;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
	

}
