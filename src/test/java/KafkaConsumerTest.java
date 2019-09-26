import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * 
* Title: KafkaConsumerTest
* Description: 
*  kafka消费者 demo
* Version:1.0.0  
* @author pancm
* @date 2018年1月26日
 */
public class KafkaConsumerTest implements Runnable {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
    private final KafkaConsumer<String, String> consumer;
    private ConsumerRecords<String, String> msgList;
    private final String topic;
    private static final String GROUPID = "groupB";

    public KafkaConsumerTest(String topicName) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.253.36.37:9092");
        props.put("group.id", GROUPID);
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        this.consumer = new KafkaConsumer<String, String>(props);
        this.topic = topicName;
        this.consumer.subscribe(Arrays.asList(topic));
    }

    @Override
    public void run() {
        int messageNo = 1;
        System.out.println("---------开始消费---------");
        try {
            for (;;) {
                    msgList = consumer.poll(1000);
                    if(null!=msgList&&msgList.count()>0){
                    for (ConsumerRecord<String, String> record : msgList) {
                        //消费100条就打印 ,但打印的数据不一定是这个规律的
//                        if(messageNo%1==0){
                            System.err.println(messageNo+"=======receive: key = " + record.key() + ", value = " + record.value()+" offset==="+record.offset());
                            String sql = "INSERT INTO kafka_dada (key,value) VALUES ('"+record.key()+"','"+record.value()+"')";
//                    		String sql2 = ") VALUES (1";
//                    		String sql = sql1 + sql2;
//                    		System.err.println(sql);
                    		jdbcTemplate.update(sql);

//                        }
                        //当消费了1000条就退出
                        if(messageNo%2==0){
                        	 consumer.close();
                            break;
                           
                        }
                        messageNo++;
                    }
                }else{  
                    Thread.sleep(1000);
                }
            }       
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }  
    public static void main(String args[]) {
        KafkaConsumerTest test1 = new KafkaConsumerTest("CLWGPS-HW");
        Thread thread1 = new Thread(test1);
        thread1.start();
    }
}