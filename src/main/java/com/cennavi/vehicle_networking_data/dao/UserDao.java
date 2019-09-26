package com.cennavi.vehicle_networking_data.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.cennavi.vehicle_networking_data.beans.User;
import com.cennavi.vehicle_networking_data.utils.RRException;
import com.cennavi.vehicle_networking_data.utils.RongRunErrorCodeEnum;

@Component
public class UserDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	// 验证用户信息
	public Object checkUser(String username, String password) {
		try {
			String sql = "select count(id) from  user_info  where username= '" + username + "'  and password ='"
					+ password + "'";
			String sql2 = "select * from  user_info  where username= '" + username + "'  and password ='" + password
					+ "'";
			Integer k = jdbcTemplate.queryForObject(sql, Integer.class);
			if (k == 1) {
				List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql2);
				return queryForList;
			}
		} catch (Exception e) {
			return new RRException(RongRunErrorCodeEnum.SYSTEM_ERROR).getCodeMsg();
		}
		return new RRException(RongRunErrorCodeEnum.LOGIN_FAILED).getCodeMsg();

//		return "验证失败";
	}

	// 更新用户信息
	public Object updateUser(User user) {
		try {
			String sql1 = "UPDATE user_info  SET ";
			if (user.getPassword() != null && !user.getPassword().equals("")) {
				sql1 += "password='" + user.getPassword() + "'";
			}
			if (user.getPhoneNumber() != null && !user.getPhoneNumber().equals("")) {
				sql1 += ",phone_number='" + user.getPhoneNumber() + "'";
			}
			if (user.getContact() != null && !user.getContact().equals("")) {
				sql1 += ",contact='" + user.getContact() + "'";
			}
			sql1 += " ,update_time= now()  WHERE id=" + user.getId() + "";
			jdbcTemplate.update(sql1);
		} catch (Exception e) {
			return new RRException(RongRunErrorCodeEnum.SYSTEM_ERROR).getCodeMsg();
		}

		return new RRException(RongRunErrorCodeEnum.SUCCESS);
	}

	// 获取用户信息
	public Object getUser(User user) {
		List<Map<String, Object>> queryForList = new ArrayList<Map<String, Object>>();
		try {
			String sql2 = "select * from  user_info  where id = " + user.getId() + "";
			queryForList = jdbcTemplate.queryForList(sql2);
		} catch (Exception e) {
			return new RRException(RongRunErrorCodeEnum.SYSTEM_ERROR).getCodeMsg();
		}
		return queryForList;
	}
}
