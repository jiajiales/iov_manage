package com.cennavi.vehicle_networking_data.beans;

public class VehicleInfo {

	private String region;//区域
	private String licensePlate;//车牌
	private String gpsInstallTime;//gps安装时间
	private String vehicleServiceNumber;//车辆执勤编号车辆执勤编号
	private String vehicleType;//车型
	private String dateOfRegistration;//登记日期
	private String remarks;   //备注
	private Integer id;//序号
	private String personLiable;//车辆责任人
	private String personLiableTel;//车辆责任人电话
	private String currentKilometres;//行驶里程
	private String workArea;//工作区域
	private Integer bindingState;//绑定状态（1:绑定,0:未绑定）
	private Integer onlineStatus;//激活状态（1:激活,0:未激活）
	private String carId;//车辆ID
	private String updateTime;//更新时间
	private Integer idList[];//id集合
	private Integer bgroupId;//业务组id
	private String businessGroupName;//业务组名称
	private Integer cgroupId;//车组id
	private String carGroupName;//车组名称
	private String imageUrl;//图片url
	private Integer limit;//查询个数
	private Integer offset;//起始查询位置
	private String dataSort;//时间排序
	
	  
 
	public String getDataSort() {
		return dataSort;
	}
	public void setDataSort(String dataSort) {
		this.dataSort = dataSort;
	}
	public String getBusinessGroupName() {
		return businessGroupName;
	}
	public void setBusinessGroupName(String businessGroupName) {
		this.businessGroupName = businessGroupName;
	}
	public String getCarGroupName() {
		return carGroupName;
	}
	public void setCarGroupName(String carGroupName) {
		this.carGroupName = carGroupName;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public Integer getBgroupId() {
		return bgroupId;
	}
	public void setBgroupId(Integer bgroupId) {
		this.bgroupId = bgroupId;
	}
	public Integer getCgroupId() {
		return cgroupId;
	}
	public void setCgroupId(Integer cgroupId) {
		this.cgroupId = cgroupId;
	}
	public Integer[] getIdList() {
		return idList;
	}
	public void setIdList(Integer[] idList) {
		this.idList = idList;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getCarId() {
		return carId;
	}
	public void setCarId(String carId) {
		this.carId = carId;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getLicensePlate() {
		return licensePlate;
	}
	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}
	public String getGpsInstallTime() {
		return gpsInstallTime;
	}
	public void setGpsInstallTime(String gpsInstallTime) {
		this.gpsInstallTime = gpsInstallTime;
	}
	public String getVehicleServiceNumber() {
		return vehicleServiceNumber;
	}
	public void setVehicleServiceNumber(String vehicleServiceNumber) {
		this.vehicleServiceNumber = vehicleServiceNumber;
	}
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	public String getDateOfRegistration() {
		return dateOfRegistration;
	}
	public void setDateOfRegistration(String dateOfRegistration) {
		this.dateOfRegistration = dateOfRegistration;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPersonLiable() {
		return personLiable;
	}
	public void setPersonLiable(String personLiable) {
		this.personLiable = personLiable;
	}
	public String getPersonLiableTel() {
		return personLiableTel;
	}
	public void setPersonLiableTel(String personLiableTel) {
		this.personLiableTel = personLiableTel;
	}
	public String getCurrentKilometres() {
		return currentKilometres;
	}
	public void setCurrentKilometres(String currentKilometres) {
		this.currentKilometres = currentKilometres;
	}
	public String getWorkArea() {
		return workArea;
	}
	public void setWorkArea(String workArea) {
		this.workArea = workArea;
	}
	public Integer getBindingState() {
		return bindingState;
	}
	public void setBindingState(Integer bindingState) {
		this.bindingState = bindingState;
	}
	public Integer getOnlineStatus() {
		return onlineStatus;
	}
	public void setOnlineStatus(Integer onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	
	
	
	
}
