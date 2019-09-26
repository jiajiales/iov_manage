package com.cennavi.vehicle_networking_data.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.cennavi.vehicle_networking_data.beans.KafkaDataInfo;
import com.cennavi.vehicle_networking_data.beans.VehicleGpsPoint;

@Component
public class KafkaDataManageDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Object findList(KafkaDataInfo kafkaDataInfo) {
		String sql = "";
		if (kafkaDataInfo.getTopic().equals("topic1")) {
			sql = "Select * From kafka_dada Where 1=1 and substring(update_time,1,19) >='"
					+ kafkaDataInfo.getStartTime() + "' and substring(update_time,1,19) <='"
					+ kafkaDataInfo.getEndTime() + "'";
		}
		if (kafkaDataInfo.getTopic().equals("topic2")) {
			sql = "Select * From kafka_dada Where 1=1 and substring(update_time,1,19) >='"
					+ kafkaDataInfo.getStartTime() + "' and substring(update_time,1,19) <='"
					+ kafkaDataInfo.getEndTime() + "'";
		}
		jdbcTemplate.update(sql);
		return "删除成功";
	}

	public Object analyticalData(List<VehicleGpsPoint> list, Integer i) {
		String sql = "";
		for (VehicleGpsPoint vehicleGpsPoint : list) {
			if (i == 2) {
				sql += "INSERT INTO lh_gps_ls  (RFID_ID,CP_HM,WD,JD,SD,GPS_SJ,GPS_ZT,FX,update_time,match) VALUES ";
			}
			if (i == 3) {
				sql += "INSERT INTO hw_gps_ls  (RFID_ID,CP_HM,WD,JD,SD,GPS_SJ,GPS_ZT,FX,update_time,match) VALUES ";
			}

			sql += "('" + vehicleGpsPoint.getRFID_ID() + "','" + vehicleGpsPoint.getCP_HM() + "','"
					+ vehicleGpsPoint.getWD() + "','" + vehicleGpsPoint.getJD() + "','" + vehicleGpsPoint.getSD()
					+ "','" + vehicleGpsPoint.getGPS_SJ() + "','" + vehicleGpsPoint.getGPS_ZT() + "','"
					+ vehicleGpsPoint.getFX() + "',now()," + vehicleGpsPoint.getMATCH()
					+ ") ON conflict (CP_HM, GPS_SJ) DO NOTHING;";
		}

		sql = sql.substring(0, sql.length() - 1);
		if (sql != null) {
			jdbcTemplate.update(sql);
		}
		return null;
	}

	public Object analyticalDataSS(Map<String, VehicleGpsPoint> linkList, Integer i) {

		List<VehicleGpsPoint> list = new ArrayList<VehicleGpsPoint>();
		List<VehicleGpsPoint> list1 = new ArrayList<VehicleGpsPoint>();
		for (String key : linkList.keySet()) {
			list1.add(linkList.get(key));
		}

		String sql = "select *  from  gps_ss  ";
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
		String sql2 = "";
		for (Map<String, Object> map : queryForList) {
			for (String key : linkList.keySet()) {
				VehicleGpsPoint vehicleGpsPoint = linkList.get(key);

				if (vehicleGpsPoint.getCP_HM().equals(map.get("cp_hm"))) {
					sql2 += "UPDATE gps_ss   SET ";

					if (vehicleGpsPoint.getRFID_ID() != null && !vehicleGpsPoint.getRFID_ID().equals("")) {
						sql2 += "RFID_ID='" + vehicleGpsPoint.getRFID_ID() + "'";
					}
//						if (vehicleGpsPoint.getCP_HM() != null && !vehicleGpsPoint.getCP_HM().equals("")) {
//							sql2 += ",CP_HM='" + vehicleGpsPoint.getCP_HM() + "'";
//						}
					if (vehicleGpsPoint.getWD() != null && !vehicleGpsPoint.getWD().equals("")) {
						sql2 += ",WD='" + vehicleGpsPoint.getWD() + "'";
					}
					if (vehicleGpsPoint.getJD() != null && !vehicleGpsPoint.getJD().equals("")) {
						sql2 += ",JD='" + vehicleGpsPoint.getJD() + "'";
					}
					if (vehicleGpsPoint.getSD() != null && !vehicleGpsPoint.getSD().equals("")) {
						sql2 += ",SD='" + vehicleGpsPoint.getSD() + "'";
					}
					if (vehicleGpsPoint.getGPS_SJ() != null && !vehicleGpsPoint.getGPS_SJ().equals("")) {
						sql2 += ",GPS_SJ='" + vehicleGpsPoint.getGPS_SJ() + "'";
					}
					if (vehicleGpsPoint.getFX() != null && !vehicleGpsPoint.getFX().equals("")) {
						sql2 += ",FX='" + vehicleGpsPoint.getFX() + "'";
					}
					if (vehicleGpsPoint.getMATCH() != null && !vehicleGpsPoint.getMATCH().equals("")) {
						sql2 += ",MATCH='" + vehicleGpsPoint.getMATCH() + "'";
					}
					sql2 += " ,update_time= now()  WHERE CP_HM= '" + vehicleGpsPoint.getCP_HM() + "';";

					list.add(vehicleGpsPoint);
				}

			}

		}
		list1.removeAll(list);

		jdbcTemplate.update(sql2);
		String sql3 = "INSERT INTO gps_ss  (RFID_ID,CP_HM,WD,JD,SD,GPS_SJ,GPS_ZT,FX,bgroup_id,update_time,match) VALUES";
		if (list1.size() > 0) {
			for (VehicleGpsPoint vehicleGpsPoint : list1) {
				sql3 += "('" + vehicleGpsPoint.getRFID_ID() + "','" + vehicleGpsPoint.getCP_HM() + "','"
						+ vehicleGpsPoint.getWD() + "','" + vehicleGpsPoint.getJD() + "','" + vehicleGpsPoint.getSD()
						+ "','" + vehicleGpsPoint.getGPS_SJ() + "','" + vehicleGpsPoint.getGPS_ZT() + "','"
						+ vehicleGpsPoint.getFX() + "'," + i + ",now()," + vehicleGpsPoint.getMATCH() + "),";
			}
			sql3 = sql3.substring(0, sql3.length() - 1);
			jdbcTemplate.update(sql3);
		}

		return null;
	}

	public List<VehicleGpsPoint> findHWCp(List<VehicleGpsPoint> list) {
		String sql0 = "SELECT cp,clid from hw_cl_manege WHERE clid in";
		String es = "(";
		for (VehicleGpsPoint vehicleGpsPoint : list) {
			es += "'" + vehicleGpsPoint.getRFID_ID() + "',";
		}
		es = es.substring(0, es.length() - 1) + ")";
		if (list.size() > 0) {
			sql0 += es;
		}
		List<Map<String, Object>> queryForList0 = jdbcTemplate.queryForList(sql0);
		for (Map<String, Object> map : queryForList0) {
			for (VehicleGpsPoint vehicleGpsPoint : list) {
				if (map.get("clid").equals(vehicleGpsPoint.getRFID_ID())) {
					vehicleGpsPoint.setCP_HM((String) map.get("cp"));
				}
			}
		}
		return list;

	}

}
