//package shit;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by 60195 on 2019/9/8.
 */
public class KafkaTest {
    private  org.apache.kafka.clients.producer.KafkaProducer<String, String> producer;
    private  String topic;

    public KafkaTest(String topicName){
        Properties props = new Properties();
//        117.48.214.8:8003
//        10.253.36.39:3389
        props.put("bootstrap.servers", "10.253.36.37:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        this.producer = new org.apache.kafka.clients.producer.KafkaProducer<String, String>(props);
        this.topic = topicName;
    }

    public static void main(String[] args) {
        KafkaTest kafkaProducer = new KafkaTest("CLW_GPS");
        
    Map<String,Object> map = new HashMap<String,Object>();
        Map<String,String> mapNew = new HashMap<String,String>();
        map.put("YX_SJ", "");
        map.put("RFID_ID", "861996030355124");
        map.put("CP_HM", "粤B6TN40");
        map.put("WD", "23.085484");
        map.put("JD", "113.203727");
        map.put("HB", "");
        map.put("SD", 44);
        map.put("LC", "");
        map.put("GPS_SJ", "1560815610");
        map.put("GPS_ZT", 1);
        map.put("SJ_XM", "");
        map.put("SJ_DH", "");
        map.put("SJGX_SJ", "");
        map.put("SJCX_SJ", "");

        for (String string : map.keySet()) {
            mapNew.put(string, map.get(string).toString());
        }
        
        JSONObject jsonObject=JSONObject.fromObject(mapNew);
        
        String string = jsonObject.toString();
        System.err.println(string);

        kafkaProducer.run("k",string);
//        JSONObject   json = JSONObject.p
//        String="{\"licensePlate\":\"xule11234\",\"carId\":12344,\"vehicleType\":\"垃圾车\",\"company\":\"世纪高通\",\"vehicleBrigade\":\"南山1队\"}"
    }
    
    public void run(String key, String string) {
    	
    	for(int i=0;i<1;i++) {
    	String a=String.valueOf(i);
    	String b=String.valueOf(i+10);
        producer.send(new ProducerRecord<String, String>(topic, "101",string));
        System.out.println("--------send to kafka, key: "+"101");
        System.out.println("--------send to kafka, value: "+string);
        
    	}
    	 producer.close();
    }
}
