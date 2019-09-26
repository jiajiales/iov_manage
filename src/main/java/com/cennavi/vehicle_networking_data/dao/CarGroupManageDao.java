package com.cennavi.vehicle_networking_data.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.cennavi.vehicle_networking_data.beans.CarGroupInfo;
import com.cennavi.vehicle_networking_data.utils.RRException;
import com.cennavi.vehicle_networking_data.utils.RongRunErrorCodeEnum;

@Component
public class CarGroupManageDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Object findCarGroup(CarGroupInfo carGroupInfo) {
		// TODO Auto-generated method stub
		try {
			String sql = "SELECT * FROM ( select  t1.name,t1.work_area,t1.id, substring(t1.update_time,0,20)  as update_time ,t1.bgroup_id,t2.name as bgroupName from car_group_manage t1  LEFT JOIN business_group  t2 on t1.bgroup_id=t2.id  where  1=1  ";
			if (carGroupInfo.getBgroupId() != null && carGroupInfo.getBgroupId() != 0) {
				sql += "and t1.bgroup_id= " + carGroupInfo.getBgroupId() + "";
			}
			if (carGroupInfo.getName() != null && !carGroupInfo.getName().equals("")) {
				sql += "and t1.name='" + carGroupInfo.getName() + "'";
			}
			if (carGroupInfo.getId() != null && !carGroupInfo.getId().equals("")) {
				sql += "and t1.id=" + carGroupInfo.getId() + "";
			}
			if (carGroupInfo.getDataSort() != null && !carGroupInfo.getDataSort().equals("")) {
				sql += "ORDER BY t1.update_time  " + carGroupInfo.getDataSort() + "";
			}
			if (carGroupInfo.getNameSort() != null && !carGroupInfo.getNameSort().equals("")) {
				sql += "ORDER BY t1.name  " + carGroupInfo.getNameSort() + "";
			}
			if (carGroupInfo.getBgroupSort() != null && !carGroupInfo.getBgroupSort().equals("")) {
				sql += "ORDER BY t2.name  " + carGroupInfo.getBgroupSort() + "";
			}

			if (carGroupInfo.getLimit() != null && carGroupInfo.getOffset() != null) {
				sql += " LIMIT " + carGroupInfo.getLimit() + " OFFSET " + carGroupInfo.getOffset() + "";
			}
			sql += ") T , (select count(id) as count FROM car_group_manage WHERE 1=1";

			if (carGroupInfo.getBgroupId() != null && carGroupInfo.getBgroupId() != 0) {
				sql += "and bgroup_id= " + carGroupInfo.getBgroupId() + "";
			}
			if (carGroupInfo.getName() != null && !carGroupInfo.getName().equals("")) {
				sql += "and name='" + carGroupInfo.getName() + "'";
			}
			sql += ") T2";

			System.err.println(sql);
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
			return queryForList;
		} catch (Exception e) {
			return new RRException(RongRunErrorCodeEnum.SYSTEM_ERROR).getCodeMsg();
		}

	}

	public Object updateCarGroup(CarGroupInfo carGroupInfo) {

		try {
			String sql = "UPDATE car_group_manage  SET ";
			if (carGroupInfo.getBgroupId() != null && !carGroupInfo.getBgroupId().equals("")) {
				sql += "bgroup_id='" + carGroupInfo.getBgroupId() + "'";
			}
			if (carGroupInfo.getName() != null && !carGroupInfo.getName().equals("")) {
				sql += ",name='" + carGroupInfo.getName() + "'";
			}
			if (carGroupInfo.getWorkArea() != null && !carGroupInfo.getWorkArea().equals("")) {
				sql += ",work_area='" + carGroupInfo.getWorkArea() + "'";
			}
			sql += ",update_time=now() where id=" + carGroupInfo.getId() + "";
			jdbcTemplate.update(sql);
		} catch (Exception e) {
			return new RRException(RongRunErrorCodeEnum.SYSTEM_ERROR).getCodeMsg();
		}

		return new RRException(RongRunErrorCodeEnum.SUCCESS).getCodeMsg();
	}

	public Object addCarGroup(CarGroupInfo carGroupInfo) {
		System.err.println(carGroupInfo.getBgroupId());

		try {
			if (carGroupInfo.getBgroupId() != null && carGroupInfo.getBgroupId() != 0) {
				String sql0 = "select  count(id)  from car_group_manage where bgroup_id=" + carGroupInfo.getBgroupId()
						+ " and name= '" + carGroupInfo.getName() + "' ";
				System.err.println(sql0);
				Integer k = jdbcTemplate.queryForObject(sql0, Integer.class);
				if (k > 0) {
					return new RRException(RongRunErrorCodeEnum.CARGROUP_DUPLICATE_NAME).getCodeMsg();
				}
			}
			String sql = "INSERT INTO car_group_manage (bgroup_id,name,work_area,update_time) VALUES";
			sql += "(" + carGroupInfo.getBgroupId() + ",'" + carGroupInfo.getName() + "','" + carGroupInfo.getWorkArea()
					+ "',now())";
			jdbcTemplate.update(sql);
		} catch (Exception e) {
			return new RRException(RongRunErrorCodeEnum.SYSTEM_ERROR).getCodeMsg();
		}

		return new RRException(RongRunErrorCodeEnum.SUCCESS).getCodeMsg();
	}

	public Object delCarGroup(CarGroupInfo carGroupInfo) {
		System.err.println("carGroupInfo:" + carGroupInfo.getId());
		try {
			String sql = "DELETE FROM car_group_manage	WHERE id =" + carGroupInfo.getId() + "";
			String sql2 = "select  count(id)  from vehicle_manage  WHERE cgroup_id=" + carGroupInfo.getId() + "   ";
			Integer k = jdbcTemplate.queryForObject(sql2, Integer.class);
			if (k > 0) {
				return new RRException(RongRunErrorCodeEnum.CARGROUP_CONTAINS_ERROT).getCodeMsg();
			}
			jdbcTemplate.update(sql);
		} catch (Exception e) {
			return new RRException(RongRunErrorCodeEnum.SYSTEM_ERROR).getCodeMsg();
		}

		return new RRException(RongRunErrorCodeEnum.SUCCESS).getCodeMsg();
	}

	public Object findBusinessGroup() {

		try {
			String sql = "select * from business_group   ";

			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);

			for (Map<String, Object> map : queryForList) {
				String sql2 = "select name,work_area as workArea,id, substring(update_time,0,20)  as updateTime from car_group_manage  where bgroup_id ="
						+ map.get("id") + " ";
				System.err.println(sql2);
				List<Map<String, Object>> carGroupList = jdbcTemplate.queryForList(sql2);
				map.put("carGroupList", carGroupList);
			}
			return queryForList;
		} catch (Exception e) {
			return new RRException(RongRunErrorCodeEnum.SYSTEM_ERROR).getCodeMsg();
		}

	}

}
