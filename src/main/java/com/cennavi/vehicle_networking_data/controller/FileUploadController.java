package com.cennavi.vehicle_networking_data.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

//import com.hot.analysis.exception.MyException;

@RestController
public class FileUploadController {
	// 获取配置文件的路径
//	@Value("${image.location.path}")
	private String resourceDir = "f:/image/";

	/**
	 * 实现文件上传
	 */
	@RequestMapping(value = "/index")
	public ModelAndView toIndex() {
		ModelAndView mv = new ModelAndView("uploadimg");
		return mv;
	}

	// 单个文件上传
	@RequestMapping("/dc/fileUpload")
	@ResponseBody
	public String fileUpload(MultipartFile file) {
		// 获取上传文件路径

		String uploadPath = file.getOriginalFilename();
		// 获取上传文件的后缀
		String fileSuffix = uploadPath.substring(uploadPath.lastIndexOf(".") + 1, uploadPath.length());
		if (fileSuffix.equals("apk")) {
			uploadPath = resourceDir;
		} else {
			// 上传目录地址
			// String uploadpath="E:/hot-manage/image/";//windows路径
			uploadPath = resourceDir;// liux路劲
		}
		// 上传文件名
		String fileName = new Date().getTime() + new Random().nextInt(100) + "." + fileSuffix;
		File savefile = new File(uploadPath + fileName);
		if (!savefile.getParentFile().exists()) {
			savefile.getParentFile().mkdirs();
		}
		try {
			file.transferTo(savefile);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (fileSuffix.equals("apk")) {
			return "/apk/" + fileName;
		} else {
			return "/image/" + fileName;
		}
	}

	// 批量上传
	@PostMapping("/dc/moreFileUpload")
	public String bacthFileUpload(MultipartFile[] file) throws Exception {
		StringBuffer buffer = new StringBuffer();
		for (MultipartFile multipartFile : file) {
			String str = fileUpload(multipartFile);
			buffer.append(str);
			buffer.append(",");
		}
		String all = buffer.substring(0, buffer.length() - 1);
		return all;
	}

	// 删除文件
	@PostMapping("/dc/deleteFile")
	public String delFile(String path) {
		String resultInfo = null;
		int lastIndexOf = path.lastIndexOf("/");
		String sb = path.substring(lastIndexOf + 1, path.length());
		sb = "f:/image/" + sb;
		File file = new File(sb);
		if (file.exists()) {
			if (file.delete()) {
				resultInfo = "1-删除成功";
			} else {
				resultInfo = "0-删除失败";
			}
		} else {
			resultInfo = "文件不存在！";
		}

		return resultInfo;
	}

	// 文件下载相关代码
	@RequestMapping("/download")
	public String downloadFile(HttpServletRequest request, HttpServletResponse response) {
		String fileName = "aim_test.txt";// 设置文件名，根据业务需要替换成要下载的文件名
		if (fileName != null) {
			// 设置文件路径
			String realPath = "D://aim//";
			File file = new File(realPath, fileName);
			if (file.exists()) {
				response.setContentType("application/force-download");// 设置强制下载不打开
				response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名
				byte[] buffer = new byte[1024];
				FileInputStream fis = null;
				BufferedInputStream bis = null;
				try {
					fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis);
					OutputStream os = response.getOutputStream();
					int i = bis.read(buffer);
					while (i != -1) {
						os.write(buffer, 0, i);
						i = bis.read(buffer);
					}
					System.out.println("success");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (bis != null) {
						try {
							bis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return null;
	}

}
