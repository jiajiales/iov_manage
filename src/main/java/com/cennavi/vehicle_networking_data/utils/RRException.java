package com.cennavi.vehicle_networking_data.utils;

import java.util.HashMap;

//
//import com.rbs.enums.RongRunErrorCodeEnum;
//import com.rbs.enums.ServeEnum;
//import com.rbs.enums.SysEnum;
// 
 
/**
 * @ClassName CustomException
 * @Author ywj
 * @Describe 自定义异常
 * @Date 2019/5/21 0021 11:58
 */
public class RRException extends RuntimeException {
    /**
     * 自定义异常信息
     */
    private String msg;
    /**
     * 状态码
     */
    private int code = 500;
  
 
 
    /**
     * 使用枚举类限制异常信息
     * @param errorCodeEnum 异常封装枚举类
     */
    public RRException(RongRunErrorCodeEnum errorCodeEnum) {
        super( errorCodeEnum.toString());
        this.msg = errorCodeEnum.getMsg();
        this.code = errorCodeEnum.getCode();
    }
 
    /**
     * 获取异常信息
     * @return
     */
    public String getMsg() {
        return msg;
    }
 
    /**
     * 获取状态码
     * @return
     */
    public int getCode() {
        return code;
    }
    
    
    /**
     * 获取异常信息
     * @return
     */
    public Object getCodeMsg() {
    	HashMap<String, Object > map = new HashMap<String, Object>();
    	map.put("errorCode", code);
    	map.put("errorMsg", msg);
        return map;
    }
 
    
}