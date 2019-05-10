package com.cennavi.audi_data_collect.util;


import com.cennavi.framework.JsonObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//import org.apache.log4j.Logger;


import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

public class JsonUtil {

//	private static Logger logger = Logger.getLogger(JsonUtil.class);
	private static final JsonObjectMapper mapper = new JsonObjectMapper();
	static {
//		mapper.disable(JsonGenerator.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		/*mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);*/
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		/*SimpleModule module = new SimpleModule("money", Version.unknownVersion());*/
		//mapper.registerModule(module);
		//mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
	}

	public static String toJson(Object obj) {
		String json = null;
		if (obj == null) {
			return null;
		}
		try {
			json = mapper.writeValueAsString(obj);
		} catch (Exception e) {
	//		logger.error(e);
			e.printStackTrace();
		}
		return json;
	}

	public static Object toObject(String json, Class ObjectClass) {
		Object object = null;
		if (ObjectClass == null) {
			ObjectClass = HashMap.class;
		}
		if (json == null) {
			return null;
		}
		if (json != null) {
			json = json.replaceAll("\'", "\"");
		}
		try {
			object = mapper.readValue(json, ObjectClass);
		} catch (Exception e) {
	//		logger.error(e);
			e.printStackTrace();
		}
		return object;
	}

    /**
     * 将list转换为json数据
     * @param totalRecord 总记录数
     * @param list 数据list
     * @return
     */
    public static String list2json(Integer totalRecord,List list){
//		GsonBuilder gsonBuilder = new GsonBuilder();
//		gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
//		gsonBuilder.registerTypeAdapter(Money.class, new MoneyAdapter());
//        Gson gjson = gsonBuilder.create();
//        String aaData=gjson.toJson(list);
        String json = "{\"total\":"+totalRecord+",\"rows\":"+JsonUtil.toJson(list)+"}";
        return json;
    }

    /**
     * 返回结构没有total,只有rows
     * @param list
     * @return
     */
    public static String list2json(List list){
        return JsonUtil.toJson(list);
    }

    /**
     * 返回页面提示json
     * @param info
     * @param status
     * @return
     */
    public static String backInfo(String info,String status){
        String msg = "{\"info\":\""+info+"\",\"status\":\""+status+"\"}";
        return msg;
    }

}
