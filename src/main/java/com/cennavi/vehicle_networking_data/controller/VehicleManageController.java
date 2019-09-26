package com.cennavi.vehicle_networking_data.controller;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cennavi.vehicle_networking_data.beans.VehicleInfo;
import com.cennavi.vehicle_networking_data.service.VehicleManageService;
import com.cennavi.vehicle_networking_data.utils.RRException;
import com.cennavi.vehicle_networking_data.utils.RongRunErrorCodeEnum;

@RestController
@RequestMapping("/vehicleInfo")

public class VehicleManageController {
	@Autowired
	private VehicleManageService vehicleManageService;
	private String sava_path = "F:/work/image/";

//	private String sava_path="/APP/rtic/tomcat2/webapps/images/";
//新增
	@RequestMapping(value = "/add")
	public Object addVehicleinfo(@RequestBody VehicleInfo vehicleInfo) throws Exception {

		return vehicleManageService.addVehicleinfo(vehicleInfo);

	}

//删除
	@RequestMapping(value = "/delete")
	public Object deleteVehicleinfo(@RequestBody VehicleInfo vehicleInfo) throws Exception {
		System.out.println(vehicleInfo.getIdList());
		return vehicleManageService.deleteVehicleinfo(vehicleInfo);

	}

	// 更新
	@RequestMapping(value = "/update")
	public Object updateVehicleinfo(@RequestBody VehicleInfo vehicleInfo) throws Exception {
		System.out.println(vehicleInfo);
		return vehicleManageService.updateVehicleinfo(vehicleInfo);
	}

	// 查询
	@RequestMapping(value = "/find")
	public Object findVehicleinfo(@RequestBody VehicleInfo vehicleInfo) throws Exception {
		return vehicleManageService.findVehicleinfo(vehicleInfo);

	}

	// 激活
	@RequestMapping(value = "/activation")
	public Object activation(@RequestBody VehicleInfo vehicleInfo) throws Exception {
		return vehicleManageService.activation(vehicleInfo);

	}

	// 文件上传
	@RequestMapping(value = "/uploadFile")
	public Object uploadFile(@RequestBody MultipartFile file, HttpServletRequest request) throws Exception {

		String fileName = null;
		String imagesUrl = null;
		try {
			String path = null;// 文件路径
			double fileSize = file.getSize();
			System.out.println("文件的大小是" + fileSize);

			byte[] sizebyte = file.getBytes();
			System.out.println("文件的byte大小是" + sizebyte.toString());
			if (file != null) {// 判断上传的文件是否为空
				String type = null;// 文件类型
//		           String fileName1 = file.getOriginalFilename();// 文件原名称
				String uploadPath = file.getOriginalFilename();// 文件原名称
				String fileSuffix = uploadPath.substring(uploadPath.lastIndexOf(".") + 1, uploadPath.length());// 获取文件名称后缀
				fileName = new Date().getTime() + new Random().nextInt(100) + "." + fileSuffix; // 随机生成文件名称
				imagesUrl = "117.48.214.8:9080/images/" + fileName;
//		       	vehicleInfo.setImageUrl("117.48.214.8:9080/images/"+fileName);
				System.out.println("上传的文件原名称:" + fileName);
				// 判断文件类型
				type = fileName.indexOf(".") != -1
						? fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length())
						: null;
				if (type != null) {// 判断文件类型是否为空

					if ("GIF".equals(type.toUpperCase()) || "PNG".equals(type.toUpperCase())
							|| "JPG".equals(type.toUpperCase())) {

						// 项目在容器中实际发布运行的根路径
						String realPath = request.getSession().getServletContext().getRealPath("/");
						// 自定义的文件名称

//		                   String 
						String trueFileName = String.valueOf(System.currentTimeMillis()) + "." + type;
						// 设置存放图片文件的路径

						path = sava_path + fileName;
						System.out.println("存放图片文件的路径:" + path);

						// 转存文件到指定的路径
						file.transferTo(new File(path));
						System.out.println("文件成功上传到指定目录下");

					}
				} else {
					System.out.println("不是我们想要的文件类型,请按要求重新上传");
					return new RRException(RongRunErrorCodeEnum.FILE_TYPE_ERROT).getCodeMsg();

				}
			}

			return imagesUrl;

		} catch (Exception e) {
			return new RRException(RongRunErrorCodeEnum.SYSTEM_ERROR).getCodeMsg();

		}
	}

}
