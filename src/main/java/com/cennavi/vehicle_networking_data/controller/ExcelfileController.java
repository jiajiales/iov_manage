package com.cennavi.vehicle_networking_data.controller;

import java.io.BufferedInputStream;
import java.io.InputStream;

//package com.hot.analysis.controller.poi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cennavi.vehicle_networking_data.beans.CarGroupInfo;
import com.cennavi.vehicle_networking_data.beans.VehicleGpsPoint;
//import com.hot.analysis.bean.dc.deviceType;
//import com.hot.analysis.service.dc.DCService;
import com.cennavi.vehicle_networking_data.service.CarGroupManageService;

@RestController
public class ExcelfileController {

//	@Autowired
//	private DCService dcService;
	// 表单导入

	@Autowired
	private CarGroupManageService carGroupManageService;

	@RequestMapping("/dc/Excelfile")

	public String upload(MultipartFile file, HttpServletRequest request) {
		try {
			List<CarGroupInfo> CarGroupInfoLists = new ArrayList<CarGroupInfo>();

			Map<Integer, String> map = new HashMap<>();
			int i = 0;

			String originalFileName = file.getOriginalFilename();
			InputStream is = file.getInputStream();
			int version = 0;

			// 判断版本号
			if (originalFileName.endsWith(".xls")) {
				version = 2003;
			} else if (originalFileName.endsWith(".xlsx")) {
				version = 2007;
			} else {
				throw new Exception("Incorrect file format,Only allowed '.xls,.xlsx' extension");
			}
			Workbook workbook = null;
			switch (version) {
			case 2003:
				POIFSFileSystem fs = new POIFSFileSystem(new BufferedInputStream(is));
				workbook = new HSSFWorkbook(fs);
				break;

			case 2007:
				workbook = new XSSFWorkbook(new BufferedInputStream(is));
				break;
			}

			int sheetIndex = workbook.getSheetIndex("Sheet1");
			Sheet sheet = workbook.getSheetAt(sheetIndex);

			System.out.println("开始");
			// 使用POI解析Excel文件
			// 如果是xls，使用HSSFWorkbook；2003年的excel 如果是xlsx，使用XSSFWorkbook 2007年excel
//	        	HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
			// 根据名称获得指定Sheet对象
//	    		HSSFSheet hssfSheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				CarGroupInfo carGroupInfo = new CarGroupInfo();
				i++;
				int rowNum = row.getRowNum();
				if (rowNum == 0) {// 跳出第一行 一般第一行都是表头没有数据意义
					continue;
				}
				if (row.getCell(4) != null) {// 第3列
					row.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
//	    		          carGroupInfo.setBgroupname(row.getCell(2).getStringCellValue());
					map.put(i, row.getCell(4).getStringCellValue());
//	    		          CarGroupInfoLists.setCode(row.getCell(2).getStringCellValue()); 
				}

			}

			System.err.println(map);
			for (Integer key : map.keySet()) {
				System.out.println(key);
				map.get(key);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "操作成功！";
	}

}