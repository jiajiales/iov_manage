package com.cennavi.audi_data_collect.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import java.util.HashMap;
import java.util.Map;

public class TileUtils {

	public static boolean is_wmts;

	public static String parseXyz2Bound(int x, int y, int z) {

		StringBuilder sb = new StringBuilder("POLYGON ((");

		if (!is_wmts) {
			double lngLeft = MercatorProjection.tileXToLongitude(x, (byte) z) - 0.00105;

			double latUp = MercatorProjection.tileYToLatitude(y, (byte) z) + 0.00105;

			double lngRight = MercatorProjection.tileXToLongitude(x + 1,
					(byte) z) + 0.00105;

			double latDown = MercatorProjection
					.tileYToLatitude(y + 1, (byte) z) - 0.00105;

			sb.append(lngLeft + " " + latUp + ", ");

			sb.append(lngRight + " " + latUp + ", ");

			sb.append(lngRight + " " + latDown + ", ");

			sb.append(lngLeft + " " + latDown + ", ");

			sb.append(lngLeft + " " + latUp + ")) ");
		} else {
			double lngLeft = WMTSProjection.tileX2Lon(x, z) - 0.00105;

			double latUp = WMTSProjection.tileY2Lat(y, (byte) z) + 0.00105;

			double lngRight = WMTSProjection.tileX2Lon(x + 1, (byte) z) + 0.00105;

			double latDown = WMTSProjection.tileY2Lat(y + 1, (byte) z) - 0.00105;

			sb.append(lngLeft + " " + latUp + ", ");

			sb.append(lngRight + " " + latUp + ", ");

			sb.append(lngRight + " " + latDown + ", ");

			sb.append(lngLeft + " " + latDown + ", ");

			sb.append(lngLeft + " " + latUp + ")) ");
		}

		return sb.toString();

	}

	public static Map<String, Object> parseXyz2BoundObject(int x, int y, int z) {

		StringBuilder sb = new StringBuilder("POLYGON ((");

		Map<String, Object> map = new HashMap<>();

		if (!is_wmts) {

			double lngLeft = MercatorProjection.tileXToLongitude(x, (byte) z) - 0.00105;

			double latUp = MercatorProjection.tileYToLatitude(y, (byte) z) + 0.00105;

			double lngRight = MercatorProjection.tileXToLongitude(x + 1,
					(byte) z) + 0.00105;

			double latDown = MercatorProjection
					.tileYToLatitude(y + 1, (byte) z) - 0.00105;

			sb.append(lngLeft + " " + latUp + ", ");

			sb.append(lngRight + " " + latUp + ", ");

			sb.append(lngRight + " " + latDown + ", ");

			sb.append(lngLeft + " " + latDown + ", ");

			sb.append(lngLeft + " " + latUp + ")) ");

			double[] bound = new double[4];

			bound[0] = lngLeft;
			bound[1] = lngRight;
			bound[2] = latDown;
			bound[3] = latUp;

			map.put("wkt", sb.toString());
			map.put("bound", bound);

		} else {
			double lngLeft = WMTSProjection.tileX2Lon(x, (byte) z) - 0.00105;

			double latUp = WMTSProjection.tileY2Lat(y, (byte) z) + 0.00105;

			double lngRight = WMTSProjection.tileX2Lon(x + 1, (byte) z) + 0.00105;

			double latDown = WMTSProjection.tileY2Lat(y + 1, (byte) z) - 0.00105;

			sb.append(lngLeft + " " + latUp + ", ");

			sb.append(lngRight + " " + latUp + ", ");

			sb.append(lngRight + " " + latDown + ", ");

			sb.append(lngLeft + " " + latDown + ", ");

			sb.append(lngLeft + " " + latUp + ")) ");

			double[] bound = new double[4];

			bound[0] = lngLeft;
			bound[1] = lngRight;
			bound[2] = latDown;
			bound[3] = latUp;

			map.put("wkt", sb.toString());
			map.put("bound", bound);
		}

		return map;

	}

	public static void convert2Piexl(int x, int y, int z, Geometry geom) {

		if (is_wmts) {

			double px = WMTSProjection.tileX2PixelX(x);

			double py = WMTSProjection.tileY2PixelY(y);

			Coordinate[] cs = geom.getCoordinates();

			byte zoom = (byte) z;

			for (Coordinate c : cs) {
				c.x = (int) (((WMTSProjection.lon2PixelX(c.x, zoom)) - px) * 16);

				c.y = (int) (((WMTSProjection.lat2PixelY(c.y, zoom)) - py) * 16);

				c.z = 218;
			}
			
		} else {

			double px = MercatorProjection.tileXToPixelX(x);

			double py = MercatorProjection.tileYToPixelY(y);

			Coordinate[] cs = geom.getCoordinates();

			byte zoom = (byte) z;

			for (Coordinate c : cs) {
				c.x = (int) (((MercatorProjection.longitudeToPixelX(c.x, zoom)) - px) * 16);

				c.y = (int) (((MercatorProjection.latitudeToPixelY(c.y, zoom)) - py) * 16);

				c.z = 218;
			}
		}

	}

	public static void convert2Piexl2lnglat(int x, int y, int z, Geometry geom) {

		double px = MercatorProjection.tileXToPixelX(x);

		double py = MercatorProjection.tileYToPixelY(y);

		Coordinate[] cs = geom.getCoordinates();

		byte zoom = (byte) z;

		for (Coordinate c : cs) {
			// c.x = (int)(((MercatorProjection.longitudeToPixelX(c.x, zoom)) -
			// px) * 16);
			//
			// c.y = (int)(((MercatorProjection.latitudeToPixelY(c.y, zoom)) -
			// py) * 16);
			//
			// c.z = 218;

			c.x = MercatorProjection.longitudeToPixelX(c.x, zoom);
			//
			c.y = MercatorProjection.latitudeToPixelY(c.y, zoom);

			c.z = 218;
		}

	}

	/**
	 * @param args
	 */
	// public static void main(String[] args) throws Exception {
	//
	// // WKTReader reader = new WKTReader();
	// //
	// // Geometry geom =
	// reader.read("POLYGON ((101.25 48.92249926375824, 112.5 48.92249926375824, 112.5 40.97989806962013, 101.25 40.97989806962013, 101.25 48.92249926375824))");
	// //
	// //// System.out.println(MercatorProjection.longitudeToTileX((101.25 +
	// 112.5)/2, (byte)5));
	// ////
	// ////
	// System.out.println(MercatorProjection.latitudeToTileY((48.92249926375824
	// + 40.97989806962013)/2, (byte)5));
	// //
	// // convert2Piexl(25, 11, 5, geom);
	//
	//
	// System.out.println(TileUtils.parseXyz2Bound(12123, 5444, 14));
	//
	// }

}
