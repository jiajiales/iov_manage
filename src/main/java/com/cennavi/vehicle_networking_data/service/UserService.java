package com.cennavi.vehicle_networking_data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cennavi.vehicle_networking_data.beans.User;
import com.cennavi.vehicle_networking_data.dao.UserDao;
import com.cennavi.vehicle_networking_data.dao.VehicleManageDao;

@Service
public class UserService {
	@Autowired
	private UserDao userDao;

	public Object check(String username, String password) {
		// TODO Auto-generated method stub
		return userDao.checkUser(username, password);
	}

	public Object updateUser(User user) {
		// TODO Auto-generated method stub
		return userDao.updateUser(user);
	}

	public Object getUser(User user) {
		// TODO Auto-generated method stub
		return userDao.getUser(user);
	}
}
