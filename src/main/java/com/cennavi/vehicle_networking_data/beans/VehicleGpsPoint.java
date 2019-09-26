package com.cennavi.vehicle_networking_data.beans;

import com.alibaba.fastjson.annotation.JSONField;

public class VehicleGpsPoint {

	private Integer YX_SJ;//运行时间
	private String RFID_ID;//RFID卡号
	private String CP_HM;//车牌号码
	private Double WD;//纬度
	private Double JD;//经度
	private String HB;//海拔
	private String SD;//速度
	private String LC;//里程长度
	private String GPS_SJ;   //GPS时间
	private Integer GPS_ZT;//GPS状态
	private String SJ_XM;//司机姓名
	private String SJ_DH;//司机电话
	private String SJGX_SJ;//数据更新时间
	private String SJCX_SJ;//数据创建时间
	private String FX;//方向
	private String brigade;//车组
	private Integer  bgroupId;//业务组id
	private Integer  MATCH;
	
	
	@JSONField(name="MATCH")
	public Integer getMATCH() {
		return MATCH;
	}
	public void setMATCH(Integer mATCH) {
		MATCH = mATCH;
	}
	 
	public Integer getBgroupId() {
		return bgroupId;
	}

	
	public void setBgroupId(Integer bgroupId) {
		this.bgroupId = bgroupId;
	}
	public String getBrigade() {
		return brigade;
	}
	public void setBrigade(String brigade) {
		this.brigade = brigade;
	}
	@JSONField(name="FX")
	public String getFX() {
		return FX;
	}
	public void setFX(String fX) {
		FX = fX;
	}
	@JSONField(name="WD")
	public Double getWD() {
		return WD;
	}
	public void setWD(Double wD) {
		WD = wD;
	}
	@JSONField(name="JD")
	public Double getJD() {
		return JD;
	}
	public void setJD(Double jD) {
		JD = jD;
	}
	public Integer getYX_SJ() {
		return YX_SJ;
	}
	public void setYX_SJ(Integer yX_SJ) {
		YX_SJ = yX_SJ;
	}
	@JSONField(name="RFID_ID")
	public String getRFID_ID() {
		return RFID_ID;
	}
	public void setRFID_ID(String rFID_ID) {
		RFID_ID = rFID_ID;
	}
	@JSONField(name="CP_HM")
	public String getCP_HM() {
		return CP_HM;
	}
	public void setCP_HM(String cP_HM) {
		CP_HM = cP_HM;
	}
 
	public String getHB() {
		return HB;
	}
	public void setHB(String hB) {
		HB = hB;
	}
	@JSONField(name="SD")
	public String getSD() {
		return SD;
	}
	public void setSD(String sD) {
		SD = sD;
	}
	public String getLC() {
		return LC;
	}
	public void setLC(String lC) {
		LC = lC;
	}
	@JSONField(name="GPS_SJ")
	public String getGPS_SJ() {
		return GPS_SJ;
	}
	public void setGPS_SJ(String gPS_SJ) {
		GPS_SJ = gPS_SJ;
	}
	@JSONField(name="GPS_ZT")
	public Integer getGPS_ZT() {
		return GPS_ZT;
	}
	public void setGPS_ZT(Integer gPS_ZT) {
		GPS_ZT = gPS_ZT;
	}
	public String getSJ_XM() {
		return SJ_XM;
	}
	public void setSJ_XM(String sJ_XM) {
		SJ_XM = sJ_XM;
	}
	public String getSJ_DH() {
		return SJ_DH;
	}
	public void setSJ_DH(String sJ_DH) {
		SJ_DH = sJ_DH;
	}
	public String getSJGX_SJ() {
		return SJGX_SJ;
	}
	public void setSJGX_SJ(String sJGX_SJ) {
		SJGX_SJ = sJGX_SJ;
	}
	public String getSJCX_SJ() {
		return SJCX_SJ;
	}
	public void setSJCX_SJ(String sJCX_SJ) {
		SJCX_SJ = sJCX_SJ;
	}
	
	
 
}
