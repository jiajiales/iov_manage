package com.cennavi.vehicle_networking_data.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cennavi.vehicle_networking_data.beans.VehicleInfo;
import com.cennavi.vehicle_networking_data.dao.VehicleManageDao;

@Service
public class VehicleManageService {
	@Autowired
	private VehicleManageDao vehicleManageDao;

	public Object addVehicleinfo(VehicleInfo vehicleInfo) {
		return vehicleManageDao.addVehicleinfo(vehicleInfo);
	}

	public Object deleteVehicleinfo(VehicleInfo vehicleInfo) {
		return vehicleManageDao.deleteVehicleinfo(vehicleInfo);
	}
	public Object updateVehicleinfo(VehicleInfo vehicleInfo) {
		return vehicleManageDao.updateVehicleinfo(vehicleInfo);
	}

	public Object findVehicleinfo(VehicleInfo vehicleInfo) {
		return vehicleManageDao.findVehicleinfo(vehicleInfo);
	}

	public Object activation(VehicleInfo vehicleInfo) {
		return vehicleManageDao.activation(vehicleInfo);
	}

}
