package com.cennavi.audi_data_collect.util;

/**
 * 支持WMTS的tile计算
 * @author user
 *
 */
public final class WMTSProjection {

	private WMTSProjection(){
		
	}
	
	private static final double MINLONGITUDE = -180.0;
	private static final double MAXLONGITUDE = 180.0;
	private static final double MINLATITUDE = -90.0;
	private static final double MAXLATITUDE = 90.0;
	
	private static final int TILESIZE = 256;
    
    /**
     * 获取不同等级单位像素的经纬度
     * @param zoom
     * @return
     */
	public static double resolution(int zoom){
		double resolution = 180.0 / TILESIZE / Math.pow(2.0, zoom - 1);	
		
		return resolution;
	}
	/**
	 * 经度转像素坐标
	 * @param lon 
	 * @param zoom 
	 * @return
	 */
	public static double lon2PixelX(double lon, int zoom){		
		lon = clip(lon, MINLONGITUDE, MAXLONGITUDE);
		double pixelx = (lon + 180.0) / resolution(zoom);
		
		return pixelx;
	}
	/**
	 * 维度转像素坐标
	 * @param lat
	 * @param zoom
	 * @return
	 */
	public static double lat2PixelY(double lat, int zoom){
		lat = clip(lat, MINLATITUDE, MAXLATITUDE);		
		double pixely = (90.0 - lat) / resolution(zoom);
		
		return pixely;
	}
	/**
	 * 经度像素坐标转瓦片坐标
	 * @param dx
	 * @param zoom
	 * @return
	 */
	public static long pixelX2TileX(double dx, int zoom){
		return (long)clip((Math.ceil(dx / TILESIZE) -1) , 0 , Math.pow(2.0, zoom) - 1);
	}
	/**
	 * 维度像素坐标转瓦片坐标
	 * @param dy
	 * @param zoom
	 * @return
	 */
	public static long pixelY2TileY(double dy, int zoom){
		return (long)clip((Math.ceil(dy / TILESIZE) - 1) , 0 , Math.pow(2.0, zoom) - 1);
	}
	/**
	 * 经度转瓦片坐标
	 * @param lon
	 * @param zoom
	 * @return
	 */
	public static long lon2TileX(double lon, int zoom){
		double dx = lon2PixelX(lon, zoom);
		return pixelX2TileX(dx, zoom);
	}
	/**
	 * 维度转瓦片坐标
	 * @param lat
	 * @param zoom
	 * @return
	 */
	public static long lat2TileY(double lat, int zoom){
		double dy = lat2PixelY(lat, zoom);
		return pixelY2TileY(dy, zoom);
				
	}
	/**
	 * 瓦片坐标转像素坐标X
	 * @param tx
	 * @return
	 */
	public static double tileX2PixelX(long tx){
		return tx * TILESIZE;
	}
	/**
	 * 瓦片坐标转像素坐标Y
	 * @param ty
	 * @return
	 */
	public static double tileY2PixelY(long ty){
		return ty * TILESIZE;
	}
	/**
	 * 像素坐标X转经度
	 * @param dx
	 * @param zoom
	 * @return
	 */
	public static double pixelX2Lon(double dx, int zoom){
		double lon = dx * resolution(zoom) - 180.0;
		return clip(lon, MINLONGITUDE, MAXLONGITUDE);
	}
	/**
	 * 像素坐标Y转维度
	 * @param dy
	 * @param zoom
	 * @return
	 */
	public static double pixelY2Lat(double dy , int zoom){
		double lat = 90 - dy * resolution(zoom);
		return clip(lat, MINLATITUDE, MAXLATITUDE);
	}
	/**
	 * 瓦片坐标X转经度
	 * @param tx
	 * @param zoom
	 * @return
	 */
	public static double tileX2Lon(long tx, int zoom){
		double dx = tileX2PixelX(tx);
		
		return pixelX2Lon(dx, zoom);
	}
	/**
	 * 瓦片坐标Y转维度
	 * @param ty
	 * @param zoom
	 * @return
	 */
	public static double tileY2Lat(long ty, int zoom){
		double dy = tileY2PixelY(ty);
		
		return pixelY2Lat(dy, zoom);
	}
	/**
	 * 通过瓦片坐标获取像素  envelope
	 * @param tx
	 * @param ty
	 * @return
	 */
	public static double[] pixelBoundTile(long tx , long ty){
		double pixelminx = tileX2PixelX(tx);
		double pixelminy = tileY2PixelY(ty);
		
		double pixelmaxx = tileX2PixelX(tx + 1);
		double pixelmaxy = tileY2PixelY(ty + 1);
		
		
		return new double[] {pixelminx , pixelmaxx , pixelminy , pixelmaxy};
	}
	/**
	 * 通过瓦片坐标获取经纬度envelope
	 * @param tx
	 * @param ty
	 * @param zoom
	 * @return
	 */
	public static double[] lonlatBoundTile(long tx , long ty , int zoom){
		double[] bound = pixelBoundTile(tx, ty);
		
		double lonmin = pixelX2Lon(bound[0], zoom);
		double lonmax = pixelX2Lon(bound[1], zoom);
		
		double latmin = pixelY2Lat(bound[3], zoom);
		double latmax = pixelY2Lat(bound[2], zoom);
		
		return new double[] {lonmin, lonmax, latmin, latmax};
	}
    /**
     * 	
     * @param value
     * @param minValue
     * @param maxValue
     * @return
     */
	public static double clip(double value, double minValue, double maxValue){
		return Math.min(Math.max(value, minValue), maxValue);
	}
}
