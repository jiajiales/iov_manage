package com.cennavi.vehicle_networking_data.utils;

import org.apache.http.HttpException;

//import com.rbs.enums.HttpCodeEnum;
//import com.rbs.enums.RongRunErrorCodeEnum;
//import com.rbs.enums.ServeEnum;
//import com.rbs.enums.SysEnum;
//import com.rbs.exception.HttpException;
//import com.rbs.exception.RRException;
 
 
/**
 * @ClassName exceptiontest
 * @Author ywj
 * @Describe
 * @Date 2019/5/28 0028 9:36
 */
public class exceptiontest {
 
 
    public static void main(String[] args) throws HttpException {
 
        ss();
    }
 
    public static void ss() throws HttpException {
        String str = null;
        if (str == null) {
        	 RRException a=  new RRException(RongRunErrorCodeEnum.PARAM_EMPTY);
             
             System.err.println("Msg:"+a.getMsg());
             System.err.println("Message:"+a.getMessage());
             System.err.println("Code:"+ a.getCode());
             System.err.println("Cause:"+ a.getCause());
             
            throw new RRException(RongRunErrorCodeEnum.PARAM_EMPTY);
            
        }
       
        throw new RRException(RongRunErrorCodeEnum.PARAM_EMPTY);
    }
 
}