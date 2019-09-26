package com.cennavi.vehicle_networking_data.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cennavi.vehicle_networking_data.beans.User;
import com.cennavi.vehicle_networking_data.service.UserService;
import com.cennavi.vehicle_networking_data.utils.RRException;
import com.cennavi.vehicle_networking_data.utils.RongRunErrorCodeEnum;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	// 验证用户信息
	@RequestMapping(value = "/check")
	public Object checkUser(@RequestBody User user) {
		if (user.getUsername() == null || user.getUsername().equals("")) {
			return new RRException(RongRunErrorCodeEnum.PARAM_EMPTY).getCodeMsg();

		}
		if (user.getPassword() == null || user.getPassword().equals("")) {
			return new RRException(RongRunErrorCodeEnum.PARAM_EMPTY).getCodeMsg();
		}

		return userService.check(user.getUsername(), user.getPassword());
	}

	// 更新用户信息
	@RequestMapping(value = "/update")
	public Object updateUser(@RequestBody User user) {
		return userService.updateUser(user);
	}

	// 查询用户信息
	@RequestMapping(value = "/get")
	public Object getUser(@RequestBody User user) {
		return userService.getUser(user);
	}

}
