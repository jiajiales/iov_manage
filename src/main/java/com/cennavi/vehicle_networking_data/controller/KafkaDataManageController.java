package com.cennavi.vehicle_networking_data.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cennavi.vehicle_networking_data.beans.KafkaDataInfo;
import com.cennavi.vehicle_networking_data.beans.VehicleInfo;
import com.cennavi.vehicle_networking_data.service.KafkaDataManageService;
import com.cennavi.vehicle_networking_data.service.VehicleManageService;

import io.netty.util.Constant;

@RestController
@RequestMapping("/data")
public class KafkaDataManageController {
	@Autowired
	private KafkaDataManageService kafkaDataManageService;
	JSONArray jsonarrayHW = new JSONArray();

	// 绿化监听
	@KafkaListener(topics = { "LH_GPS" }) // 监听topic机制 如果topic的数据改变了 就执行该语句
	public void consumer2(ConsumerRecord<?, ?> record) {
		System.err.println("record.key():" + record.key());
		kafkaDataManageService.analyticalDataPublic(record.value().toString());
	}

	// 监听环卫机制
	@KafkaListener(topics = { "szcg_hw_car" })
	public void consumer1(ConsumerRecord<?, ?> record) throws ParseException {
		String str1 = StringEscapeUtils.unescapeJava(record.value().toString());
		str1 = str1.substring(1, str1.length());
		str1 = str1.substring(0, str1.length() - 1);
		JSONObject json = JSONObject.parseObject(str1);
		jsonarrayHW.add(json);
		System.out.println(jsonarrayHW.size());
		if (jsonarrayHW.size() >= 100) {
			System.err.println(jsonarrayHW.toString());
			kafkaDataManageService.analyticaDataHW(jsonarrayHW.toString());
			jsonarrayHW = new JSONArray();
		}
	}

	@RequestMapping(value = "/findList")
	public Object findList(@RequestBody String requestParam) throws Exception {
		kafkaDataManageService.analyticaDataHW(requestParam.toString());
		return null;
	}

	@RequestMapping(value = "/analytical")
	public Object analyticalData(@RequestBody String requestParam) throws Exception {
		return kafkaDataManageService.analyticalDataPublic(requestParam);

	}
//	@RequestMapping(value = "/analyticalSS")
//	public Object analyticalSS(@RequestBody String requestParam) throws Exception {
//		return kafkaDataManageService.analyticalDataSS(requestParam,2);
//	}

	@RequestMapping(value = "/analyticalHWSS")
	public Object analyticalHWSS(@RequestBody String requestParam) throws Exception {
		return kafkaDataManageService.analyticalHWSS(requestParam, 3);
	}

}
