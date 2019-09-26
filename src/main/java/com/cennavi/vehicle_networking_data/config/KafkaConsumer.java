package com.cennavi.vehicle_networking_data.config;

//import com.cennavi.utils.CoordinateTransformUtils;
//import com.cennavi.utils.Point;
//import com.cennavi.utils.RedisUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.cennavi.vehicle_networking_data.beans.Point;
import com.cennavi.vehicle_networking_data.controller.KafkaDataManageController;
import com.cennavi.vehicle_networking_data.service.KafkaDataManageService;
import com.cennavi.vehicle_networking_data.util.CoordinateTransformUtils;

import org.springframework.kafka.annotation.KafkaListener;

/**
 * Created by 60195 on 2019/9/6.
 */
@Component
public class KafkaConsumer{

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
	private JdbcTemplate jdbcTemplate;
    @Autowired
	private KafkaDataManageService kafkaDataManageService;

//    String value;
//    String data[];

    @KafkaListener(topics = {"LH_GPS"} )//监听topic机制  如果topic的数据改变了   就执行该语句
    public void consumer2(ConsumerRecord<?, ?> record) {

        String value = record.value().toString();
//        data = value.split(",");

        if(record.key() == null){
            redisTemplate.opsForValue().set("null",value);
        }else {
//        	System.out.println("record.key():"+record.key());
//        	System.out.println("record.value():"+record.value().toString());
        	kafkaDataManageService.analyticalData(record.value().toString());
        	kafkaDataManageService.analyticalDataSS(record.value().toString(),2);
//            redisTemplate.opsForValue().set("Gps_"+record.key().toString(),value);           //方正的key是手台id
//            String sql = "INSERT INTO kafka_dada (key,value,update_time) VALUES ('"+record.key()+"','"+record.value()+"',now())";
//                    		System.err.println(sql);
//                    		jdbcTemplate.update(sql);
            
        }

    }

    @KafkaListener(topics = {"zongzhi-gps"} )
    public void consumer1(ConsumerRecord<?, ?> record) {

//        System.out.println("    key:" + record.key());
//        System.out.println("    value:"+record.value());
        String value = record.value().toString();
        System.out.println("----zongzhi-gps--before: "+value);
        String[] data = value.split(",");
        Point point=new Point(Double.parseDouble(data[6]),Double.parseDouble(data[7]));
        Point bd09ToWgs84 = CoordinateTransformUtils.bd09ToWgs84(point.getLng(), point.getLat());
        data[6] = String.valueOf(bd09ToWgs84.getLng());
        data[7] = String.valueOf(bd09ToWgs84.getLat());

        String value2 = "";
        for(int i=0; i<data.length; i++){
            value2 += data[i]+ ",";
        }
        int index = value2.length();
        value2 = value2.substring(0,index-1);
        System.out.println("-----zongzhi-gps-after:"+value2);
        if(record.key() == null){
//            redisUtil.set("111",value);
            redisTemplate.opsForValue().set("null",value2);
        }else {
//            redisUtil.set(record.key().toString(),value);
            redisTemplate.opsForValue().set("zongGps_"+data[0],value2);          //存人的最新位置信息，所以key是人员id
            System.out.println("-------id----------- "+ data[0]);
        }
    }

    @KafkaListener(topics = {"zongzhi-security"} )
    public void consumer3(ConsumerRecord<?, ?> record) {

//        System.out.println("    key:" + record.key());
//        System.out.println("    value:"+record.value());
        String value = record.value().toString();
//        data = value.split(",");

        if(record.key() == null){
//            redisUtil.set("111",value);
            redisTemplate.opsForValue().set("null",value);
        }else {
//            redisUtil.set(record.key().toString(),value);
            redisTemplate.opsForValue().set("zongSecurity_"+record.key().toString(),value);
            System.out.println("zongzhi-security"+value);
        }
    }
}
