package com.cennavi.vehicle_networking_data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cennavi.vehicle_networking_data.beans.CarGroupInfo;
import com.cennavi.vehicle_networking_data.dao.CarGroupManageDao;

@Service

public class CarGroupManageService {
	@Autowired
	private CarGroupManageDao carGroupManageDao;

	public Object findCarGroup(CarGroupInfo carGroupInfo) {
		// TODO Auto-generated method stub
		return carGroupManageDao.findCarGroup(carGroupInfo);
	}

	public Object updateCarGroup(CarGroupInfo carGroupInfo) {
		// TODO Auto-generated method stub
		return carGroupManageDao.updateCarGroup(carGroupInfo);
	}

	public Object addCarGroup(CarGroupInfo carGroupInfo) {
		System.err.println("service");
		return carGroupManageDao.addCarGroup(carGroupInfo);
	}

	public Object delCarGroup(CarGroupInfo carGroupInfo) {
		// TODO Auto-generated method stub
		return carGroupManageDao.delCarGroup(carGroupInfo);
	}

	public Object findBusinessGroup() {
		// TODO Auto-generated method stub
		return carGroupManageDao.findBusinessGroup();
	}

}
