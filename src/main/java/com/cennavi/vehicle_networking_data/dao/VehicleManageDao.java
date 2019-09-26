package com.cennavi.vehicle_networking_data.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cennavi.vehicle_networking_data.beans.VehicleInfo;
import com.cennavi.vehicle_networking_data.utils.RRException;
import com.cennavi.vehicle_networking_data.utils.RongRunErrorCodeEnum;

@Component
public class VehicleManageDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Object addVehicleinfo(VehicleInfo vehicleInfo) {
		Integer k = 1;

		try {

			String sql1 = "INSERT INTO vehicle_manage (";
			String sql2 = ") VALUES (";
			String sql0 = "SELECT count(license_plate)  FROM vehicle_manage WHERE license_plate='"
					+ vehicleInfo.getLicensePlate() + "'";
			System.err.println(sql0);
			Integer i = jdbcTemplate.queryForObject(sql0, Integer.class);
			if (i > 0) {

				return new RRException(RongRunErrorCodeEnum.VEHICLE_DUPLICATE_NAME).getCodeMsg();
			}

			if (vehicleInfo.getLicensePlate() != null && !vehicleInfo.getLicensePlate().equals("")) {
				sql1 += "license_plate";
				sql2 += "'" + vehicleInfo.getLicensePlate() + "'";
				String sql3 = "SELECT count(id)  from   gps_ss   WHERE cp_hm= '" + vehicleInfo.getLicensePlate() + "'";
				System.out.println("sql3：" + sql3);
				k = jdbcTemplate.queryForObject(sql3, Integer.class);
				if (k > 0) {
					sql1 += ",binding_state";
					sql2 += ",1";

					if (vehicleInfo.getOnlineStatus() != null && !vehicleInfo.getOnlineStatus().equals("")) {
						sql1 += ",online_status";
						sql2 += "," + vehicleInfo.getOnlineStatus() + "";
					}
				} else {
					sql1 += ",online_status";
					sql2 += ",0";
					sql1 += ",binding_state";
					sql2 += ",0";
				}
			}

			if (vehicleInfo.getCarId() != null && !vehicleInfo.getCarId().equals("")) {
				sql1 += ",car_id";
				sql2 += ",'" + vehicleInfo.getCarId() + "'";
			}
			if (vehicleInfo.getVehicleType() != null && !vehicleInfo.getVehicleType().equals("")) {
				sql1 += ",vehicle_type";
				sql2 += ",'" + vehicleInfo.getVehicleType() + "'";
			}

			if (vehicleInfo.getBgroupId() != null && !vehicleInfo.getBgroupId().equals("")) {
				sql1 += ",bgroup_id";
				sql2 += ",'" + vehicleInfo.getBgroupId() + "'";
			}
			if (vehicleInfo.getCgroupId() != null && !vehicleInfo.getCgroupId().equals("")) {
				sql1 += ",cgroup_id";
				sql2 += ",'" + vehicleInfo.getCgroupId() + "'";
			}

			if (vehicleInfo.getPersonLiable() != null && !vehicleInfo.getPersonLiable().equals("")) {
				sql1 += ",person_liable";
				sql2 += ",'" + vehicleInfo.getPersonLiable() + "'";
			}
			if (vehicleInfo.getPersonLiableTel() != null && !vehicleInfo.getPersonLiableTel().equals("")) {
				sql1 += ",person_liable_tel";
				sql2 += ",'" + vehicleInfo.getPersonLiableTel() + "'";
			}
			if (vehicleInfo.getCurrentKilometres() != null && !vehicleInfo.getCurrentKilometres().equals("")) {
				sql1 += ",current_kilometres";
				sql2 += ",'" + vehicleInfo.getCurrentKilometres() + "'";
			}
			if (vehicleInfo.getWorkArea() != null && !vehicleInfo.getWorkArea().equals("")) {
				sql1 += ",work_area";
				sql2 += ",'" + vehicleInfo.getWorkArea() + "'";
			}
			if (vehicleInfo.getImageUrl() != null && !vehicleInfo.getImageUrl().equals("")) {
				sql1 += ",image_url";
				sql2 += ",'" + vehicleInfo.getImageUrl() + "'";
			}

			sql1 += ",update_time";
			sql2 += ", now())";
			String sql = sql1 + sql2;
//	System.out.println("sql:_____"+sql);
			System.err.println(sql);
			jdbcTemplate.update(sql);

		} catch (Exception e) {
			return new RRException(RongRunErrorCodeEnum.SYSTEM_ERROR).getCodeMsg();
		}

		if (k == 0) {

			return new RRException(RongRunErrorCodeEnum.VEHICLE_ADD_ERROR).getCodeMsg();
		}

		return new RRException(RongRunErrorCodeEnum.SUCCESS).getCodeMsg();

	}

	public Object deleteVehicleinfo(VehicleInfo vehicleInfo) {

		try {

			String sql = "DELETE FROM vehicle_manage	WHERE id in";
			if (vehicleInfo.getIdList() != null) {
				String es = "(";
				for (int i = 0; i < vehicleInfo.getIdList().length; i++) {
					es += "" + vehicleInfo.getIdList()[i] + ",";
				}
				es = es.substring(0, es.length() - 1) + ")";
				if (vehicleInfo.getIdList().length > 0) {
					sql += es;
				}
				System.err.println("sql:" + sql);
				jdbcTemplate.update(sql);

			}

		} catch (Exception e) {
			return new RRException(RongRunErrorCodeEnum.SYSTEM_ERROR).getCodeMsg();
		}

		return new RRException(RongRunErrorCodeEnum.SUCCESS).getCodeMsg();
	}

	public Object updateVehicleinfo(VehicleInfo vehicleInfo) {

		Integer k = 1;
		try {
			String sql1 = "UPDATE vehicle_manage  SET ";

			if (vehicleInfo.getLicensePlate() != null && !vehicleInfo.getLicensePlate().equals("")) {
				sql1 += "  license_plate='" + vehicleInfo.getLicensePlate() + "'";
				String sql2 = "SELECT count(id)  from   gps_ss   WHERE cp_hm= '" + vehicleInfo.getLicensePlate() + "'";

				k = jdbcTemplate.queryForObject(sql2, Integer.class);
				System.err.println("k-----------:" + k);
				if (k > 0) {

					sql1 += " , binding_state= 1";
					if (vehicleInfo.getOnlineStatus() != null && !vehicleInfo.getOnlineStatus().equals("")) {
						sql1 += " , online_status= " + vehicleInfo.getOnlineStatus() + "";
					}
				} else {

					sql1 += " , online_status= 0";
					sql1 += " , binding_state= 0";
				}
			}
			if (vehicleInfo.getCarId() != null && !vehicleInfo.getCarId().equals("")) {
				sql1 += ",car_id=" + vehicleInfo.getCarId() + "";
			}
			if (vehicleInfo.getVehicleType() != null && !vehicleInfo.getVehicleType().equals("")) {
				sql1 += ",vehicle_type='" + vehicleInfo.getVehicleType() + "'";
			}
			if (vehicleInfo.getBgroupId() != null && !vehicleInfo.getBgroupId().equals("")) {
				sql1 += ",bgroup_id=" + vehicleInfo.getBgroupId() + "";
			}
			if (vehicleInfo.getCgroupId() != null && !vehicleInfo.getCgroupId().equals("")) {
				sql1 += ",cgroup_id=" + vehicleInfo.getCgroupId() + "";
			}
			if (vehicleInfo.getPersonLiable() != null && !vehicleInfo.getPersonLiable().equals("")) {
				sql1 += ",person_liable='" + vehicleInfo.getPersonLiable() + "'";
			}
			if (vehicleInfo.getPersonLiableTel() != null && !vehicleInfo.getPersonLiableTel().equals("")) {
				sql1 += ",person_liable_tel='" + vehicleInfo.getPersonLiableTel() + "'";
			}
			if (vehicleInfo.getCurrentKilometres() != null && !vehicleInfo.getCurrentKilometres().equals("")) {
				sql1 += ",current_kilometres='" + vehicleInfo.getCurrentKilometres() + "'";
			}
			if (vehicleInfo.getWorkArea() != null && !vehicleInfo.getWorkArea().equals("")) {
				sql1 += ",work_area='" + vehicleInfo.getWorkArea() + "'";
			}
			if (vehicleInfo.getImageUrl() != null && !vehicleInfo.getImageUrl().equals("")) {
				sql1 += ",image_url='" + vehicleInfo.getImageUrl() + "'";
			}

			sql1 += " ,update_time= now()  WHERE id=" + vehicleInfo.getId() + "";
//			System.err.println(sql1);
			jdbcTemplate.update(sql1);

		} catch (Exception e) {
			return new RRException(RongRunErrorCodeEnum.SYSTEM_ERROR).getCodeMsg();
		}
//		
		if (k == 0) {
			return new RRException(RongRunErrorCodeEnum.VEHICLE_UPDATE_ERROR).getCodeMsg();
		}

		return new RRException(RongRunErrorCodeEnum.SUCCESS).getCodeMsg();
	}

	public Object findVehicleinfo(VehicleInfo vehicleInfo) {

		try {
			String sql1 = "select * from  (select t1.id,t1.license_plate, t1.online_status,t1.image_url,t1.binding_state, t1.person_liable,t1.current_kilometres,t1.car_id, t1.work_area, t1.person_liable_tel, substring(t1.update_time,0,20)  as update_time, t1.vehicle_type, t1.bgroup_id, t1.cgroup_id,t2.name as businessGroupName, t3.name as carGroupName from vehicle_manage  t1 LEFT JOIN  business_group t2 ON t1.bgroup_id=t2.id LEFT JOIN  car_group_manage t3 ON t1.cgroup_id=t3.id where  1=1 ";
			if (vehicleInfo.getLicensePlate() != null && !vehicleInfo.getLicensePlate().equals("")) {
				sql1 += "and t1.license_plate='" + vehicleInfo.getLicensePlate() + "'";
			}
			if (vehicleInfo.getWorkArea() != null && !vehicleInfo.getWorkArea().equals("")) {
				sql1 += "and t1.work_area='" + vehicleInfo.getWorkArea() + "'";
			}
			if (vehicleInfo.getOnlineStatus() != null && !vehicleInfo.getOnlineStatus().equals("")) {
				sql1 += " and t1.online_status=" + vehicleInfo.getOnlineStatus() + "";
			}
			if (vehicleInfo.getBgroupId() != null && !vehicleInfo.getBgroupId().equals("")) {
				sql1 += " and t1.bgroup_id=" + vehicleInfo.getBgroupId() + "";
			}
			if (vehicleInfo.getCgroupId() != null && !vehicleInfo.getCgroupId().equals("")) {
				sql1 += " and t1.cgroup_id=" + vehicleInfo.getCgroupId() + "";
			}
			if (vehicleInfo.getBindingState() != null && !vehicleInfo.getBindingState().equals("")) {
				sql1 += " and t1.binding_state=" + vehicleInfo.getBindingState() + "";
			}

			if (vehicleInfo.getDataSort() != null && !vehicleInfo.getDataSort().equals("")) {
				sql1 += " ORDER BY t1.update_time " + vehicleInfo.getDataSort() + "";
			}

			if (vehicleInfo.getLimit() != null && vehicleInfo.getOffset() != null) {
				sql1 += " LIMIT " + vehicleInfo.getLimit() + " OFFSET " + vehicleInfo.getOffset() + "";
			}

			sql1 += ") T1,(SELECT count(id) FROM  vehicle_manage where 1=1 ";
			if (vehicleInfo.getLicensePlate() != null && !vehicleInfo.getLicensePlate().equals("")) {
				sql1 += "and license_plate='" + vehicleInfo.getLicensePlate() + "'";
			}
			if (vehicleInfo.getWorkArea() != null && !vehicleInfo.getWorkArea().equals("")) {
				sql1 += "and  work_area='" + vehicleInfo.getWorkArea() + "'";
			}
			if (vehicleInfo.getOnlineStatus() != null && !vehicleInfo.getOnlineStatus().equals("")) {
				sql1 += " and  online_status=" + vehicleInfo.getOnlineStatus() + "";
			}
			if (vehicleInfo.getBgroupId() != null && !vehicleInfo.getBgroupId().equals("")) {
				sql1 += " and  bgroup_id=" + vehicleInfo.getBgroupId() + "";
			}
			if (vehicleInfo.getCgroupId() != null && !vehicleInfo.getCgroupId().equals("")) {
				sql1 += " and  cgroup_id=" + vehicleInfo.getCgroupId() + "";
			}

			sql1 += ")  T2";

			System.err.println(sql1);
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql1);
//			System.out.println(queryForList);
			return queryForList;

		} catch (Exception e) {
			return new RRException(RongRunErrorCodeEnum.SYSTEM_ERROR).getCodeMsg();
		}

	}

	public Object activation(VehicleInfo vehicleInfo) {
		try {

			String sql = "update  vehicle_manage  set binding_state =1,online_status=1  where id in";
			if (vehicleInfo.getIdList() != null) {
				String es = "(";
				for (int i = 0; i < vehicleInfo.getIdList().length; i++) {
					es += "" + vehicleInfo.getIdList()[i] + ",";
				}
				es = es.substring(0, es.length() - 1) + ")";
				if (vehicleInfo.getIdList().length > 0) {
					sql += es;
					String sql0 = "SELECT license_plate from vehicle_manage WHERE id in " + es
							+ " and binding_state =0";
					System.err.println("sql0:" + sql0);
					List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql0);
					if (queryForList.size() == 0) {
						jdbcTemplate.update(sql);
						return new RRException(RongRunErrorCodeEnum.SUCCESS).getCodeMsg();
					}
					String es2 = "(";
					int i = 0;
					for (Map<String, Object> map : queryForList) {
						i++;
						es2 += "'" + map.get("license_plate") + "',";
					}
					es2 = es2.substring(0, es2.length() - 1) + ")";
					String sql1 = "SELECT count(CP_HM) from gps_ss WHERE CP_HM in " + es2 + "";
					System.err.println("sql1:" + sql1);
					Integer k = jdbcTemplate.queryForObject(sql1, Integer.class);
					if (k < i) {
						return new RRException(RongRunErrorCodeEnum.VEHICLE_ACTIVATION_ERROR).getCodeMsg();
					}
					jdbcTemplate.update(sql);
				}

			}

		} catch (Exception e) {
			return new RRException(RongRunErrorCodeEnum.SYSTEM_ERROR).getCodeMsg();
		}

		return new RRException(RongRunErrorCodeEnum.SUCCESS).getCodeMsg();
	}
}
