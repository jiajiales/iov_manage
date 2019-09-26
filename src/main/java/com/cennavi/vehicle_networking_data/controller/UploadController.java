package com.cennavi.vehicle_networking_data.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cennavi.vehicle_networking_data.utils.RRException;
import com.cennavi.vehicle_networking_data.utils.RongRunErrorCodeEnum;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Date;
import java.util.Random;

@RestController
public class UploadController {

	private String sava_path = "/mydata/tomcat2/webapps/images";

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
//			           String fileName1 = file.getOriginalFilename();// 文件原名称
				String uploadPath = file.getOriginalFilename();// 文件原名称
				String fileSuffix = uploadPath.substring(uploadPath.lastIndexOf(".") + 1, uploadPath.length());// 获取文件名称后缀
				fileName = new Date().getTime() + new Random().nextInt(100) + "." + fileSuffix; // 随机生成文件名称
				imagesUrl = "szzhcg.com/images/" + fileName;
//			       	vehicleInfo.setImageUrl("117.48.214.8:9080/images/"+fileName);
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