package com.cennavi.vehicle_networking_data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cennavi.vehicle_networking_data.beans.CarGroupInfo;
import com.cennavi.vehicle_networking_data.beans.User;
import com.cennavi.vehicle_networking_data.service.CarGroupManageService;
import com.cennavi.vehicle_networking_data.service.UserService;

@RestController
@RequestMapping("/carGroup")
public class CarGroupManageController {
	@Autowired
	private CarGroupManageService carGroupManageService;

	// 查询业务组
	@RequestMapping(value = "/findBusinessGroup")
	public Object findBusinessGroup() {
		return carGroupManageService.findBusinessGroup();
	}

	// 查询车组
	@RequestMapping(value = "/findCarGroup")
	public Object findCarGroup(@RequestBody CarGroupInfo carGroupInfo) {
		return carGroupManageService.findCarGroup(carGroupInfo);
	}

	// 修改车组
	@RequestMapping(value = "/update")
	public Object updateCarGroup(@RequestBody CarGroupInfo carGroupInfo) {
		return carGroupManageService.updateCarGroup(carGroupInfo);
	}

	// 新增车组
	@RequestMapping(value = "/add")
	public Object addCarGroup(@RequestBody CarGroupInfo carGroupInfo) {
		return carGroupManageService.addCarGroup(carGroupInfo);
	}

	// 删除车组
	@RequestMapping(value = "/del")
	public Object delCarGroup(@RequestBody CarGroupInfo carGroupInfo) {
		return carGroupManageService.delCarGroup(carGroupInfo);
	}

}
