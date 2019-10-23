package com.cennavi.vehicle_networking_data.utils;

import java.awt.geom.Point2D;

public class AdminUtil {

	// 度转1/128秒单位系数
		public static final double DEGREE2UNIT = 460800.0;
		
		// WGS单位显示范围
		public static final double DEF_MIN_LATITUDE = -85.05112878;
		public static final double DEF_MAX_LATITUDE = 85.05112878;
		public static final double DEF_MIN_LONGITUDE = -180.0;
		public static final double DEF_MAX_LONGITUDE = 180.0;

		// 墨卡托最大坐标值（DEF_MAX_LATITUDE对应的墨卡托值）
		public static final double DEF_ORIGIN_SHIFT	 = 20037508.342789244;	// (DEF_PI * 6378137)  // 6378137=EarthRadius
		// WGS84球体长半轴(单位：KM)
		public static final double DEF_EARTH_RADIUS = 6378137.0;

//		public static final double DEF_BASE_MERCATOR_X 	= 0;						// X基准偏移
//		public static final double DEF_BASE_MERCATOR_Y = 0;							// Y基准偏移

		
		/**
		 * Discription:[根据两个点的经纬度算实际距离]
		 * 
		 * @param lat1
		 * @param lon1
		 * @param lat2
		 * @param lon2
		 * @return
		 * @author:GF
		 * @update:[日期YYYY-MM-DD] [更改人姓名][变更描述]
		 */
		public static double distance(double lat1, double lon1, double lat2, double lon2) 
		{
			double D2R = 0.017453;
			double e2 = 0.006739496742337;
			if (lon1 == lon2 && lat1 == lat2) 
			{
				return 0.0;
			} else {
				double fdLambda = (lon1 - lon2) * D2R;
				double fdPhi = (lat1 - lat2) * D2R;
				double fPhimean = ((lat1 + lat2) / 2.0) * D2R;
				double fTemp = 1 - e2 * (Math.pow(Math.sin(fPhimean), 2));
				double fRho = (DEF_EARTH_RADIUS * (1 - e2)) / Math.pow(fTemp, 1.5);
				double fNu = DEF_EARTH_RADIUS / (Math.sqrt(1 - e2
								* (Math.sin(fPhimean) * Math.sin(fPhimean))));
				double fz = Math.sqrt(Math.pow(Math.sin(fdPhi / 2.0), 2)
						+ Math.cos(lat2 * D2R) * Math.cos(lat1 * D2R)
						* Math.pow(Math.sin(fdLambda / 2.0), 2));
				fz = 2 * Math.asin(fz);
				double fAlpha = Math.cos(lat2 * D2R) * Math.sin(fdLambda) * 1 / Math.sin(fz);
				fAlpha = Math.asin(fAlpha);
				double fR = (fRho * fNu)
						/ ((fRho * Math.pow(Math.sin(fAlpha), 2)) + (fNu * Math.pow(Math.cos(fAlpha), 2)));
				return fz * fR;
			}
		}
		
	    /**
	     *  墨卡托坐标转WGS84坐标
	     * @param mktX：墨卡托坐标X值
	     * @param mktY：墨卡托坐标Y值
	     * @return WGS84坐标点
	     */
	    public static Point2D.Double MKT2WGS(double mktX, double mktY) 
	    {
	    	
	    	
	    	// 检查并限制坐标合法范围
	    	mktX = clip(mktX, 0 - DEF_ORIGIN_SHIFT, DEF_ORIGIN_SHIFT);
	    	mktY = clip(mktY, 0 - DEF_ORIGIN_SHIFT, DEF_ORIGIN_SHIFT);
	    	
	    	Point2D.Double result = new Point2D.Double();
			result.x = (mktX / DEF_ORIGIN_SHIFT) * 180.0;
			result.y = (mktY / DEF_ORIGIN_SHIFT) * 180.0;
			result.y = 180.0/ Math.PI * (2.0 * Math.atan(Math.exp(result.y * Math.PI / 180.0)) - Math.PI / 2.0);
	  
			return (result);

	    }

	    
	    /**
	     *  WGS84坐标转墨卡托投影坐标
	     * @param lon，WGS84坐标经度
	     * @param lat，WGS84坐标纬度
	     * @return 墨卡托坐标点
	     */
	    public static Point2D.Double WGS2MKT(double lon, double lat)
	    {
	    	// 检查并限制坐标合法范围
	    	lon = clip(lon, DEF_MIN_LONGITUDE, DEF_MAX_LONGITUDE);
	    	lat = clip(lat, DEF_MIN_LATITUDE, DEF_MAX_LATITUDE);

	    	Point2D.Double result = new Point2D.Double();
	    	result.x = lon * DEF_ORIGIN_SHIFT / 180.0;   
	    	result.y = Math.log(Math.tan((90 + lat) * Math.PI / 360.0)) / (Math.PI / 180.0) * (DEF_ORIGIN_SHIFT / 180.0);   

	        return (result);
	    }
	    
	    /**
	     * 值范围检查
	     * @param inValue
	     * @param minValue
	     * @param maxValue
	     * @return
	     */
	    public static double clip(double inValue, double minValue, double maxValue)
	    {
	    	return Math.min(Math.max(inValue, minValue), maxValue);
	    }
	    
		/**
		 * 取值范围限制
		 * @param fValue 待处理值
		 * @param fMin  允许最小值
		 * @param fMax  允许最大值
		 * @return
		 */
	    public static float clip(float fValue, float fMin, float fMax)
		{
			if(fValue < fMin)
			{
				fValue = fMin;
			}
			if(fValue > fMax)
			{
				fValue = fMax;
			}
			return fValue;
		}
	
	//获取地级市code
	public static int prefectureLevelCityCode(int code) {
		int tmpCode = code/10000;
		if(tmpCode==11 || tmpCode==12 || tmpCode==31 || tmpCode==50 || tmpCode==81 || tmpCode==82) {
			return tmpCode * 10000;
		}
		tmpCode = code / 100 * 100;
		return tmpCode;
	}
	
	//判断是否是直辖市
	public static boolean isMunicipality(int code) {
		int tmpCode = code/10000;
		if(tmpCode==11 || tmpCode==12 || tmpCode==31 || tmpCode==50 || tmpCode==81 || tmpCode==82) {
			return true;
		}
		return false;
	}
	
	//返回合适的行政代码
	public static int scalaLevelCityCode(int code) {
		int tmpCode = code/10000;
		if(tmpCode==11 || tmpCode==12 || tmpCode==31 || tmpCode==50 || tmpCode==81 || tmpCode==82) {
			return tmpCode * 10000;
		}
		if(code % 10000 == 0) {
			return tmpCode * 10000;
		}
		tmpCode = code / 100 * 100;
		return tmpCode;
	}
}
