package com.cennavi.vehicle_networking_data.beans;

public class User {
//	  user.setId(1);
//      user.setUsername("张三");
//      user.setPassword("1111");
//      user.setRediskey(userKey);
//	company	varchar	255	0	-1			default	0	0
//	phone_number	varchar	255	0	-1			default	0	0
//	brigade	varchar	255	0	-1			default	0	0

	private Integer id;//id
	private String username;//用户名
	private String password;//密码
	private String  contact;//联系人
	private String code;//code值
	private String token;//token值
	private String company;//所属单温
	private String phoneNumber;//手机号
	private String brigade;//所属大队
	private Integer bgroupId;//业务组id
	
	
	public Integer getBgroupId() {
		return bgroupId;
	}
	public void setBgroupId(Integer bgroupId) {
		this.bgroupId = bgroupId;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getBrigade() {
		return brigade;
	}
	public void setBrigade(String brigade) {
		this.brigade = brigade;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
 
	
	
}
