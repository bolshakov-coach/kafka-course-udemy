package pro.bolshakov.edu.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class MyAppProducer {
    private final static Logger log = LoggerFactory.getLogger(MyAppProducer.class);

    private static final Properties prop = new Properties();
    static {
        prop.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        prop.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prop.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }

    public static void main(String[] args) {
//        simpleProducer();
//        producerWithCallBack();
        producerWithKeys();
    }

    private static void simpleProducer() {
        try(KafkaProducer<String,String> producer = new KafkaProducer<>(prop);) {
            ProducerRecord<String, String> record = new ProducerRecord<>("first_topic", "message");
            producer.send(record);
            producer.flush();
        }
    }

    private static void producerWithCallBack(){
        try(KafkaProducer<String,String> producer = new KafkaProducer<>(prop);) {
            ProducerRecord<String, String> record = new ProducerRecord<>("first_topic", "message");
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata meta, Exception ex) {
                    if(ex == null){
                        log.info("Metadata: \n Topic: {} \n Offset: {} \n Partition: {} \n Timestamp: {}",
                                meta.topic(), meta.offset(), meta.partition(), meta.timestamp());
                    }
                    else {
                        log.error("Got error during send", ex);
                    }
                }
            });
            producer.flush();
        }
    }

    private static void producerWithKeys(){
        try(KafkaProducer<String,String> producer = new KafkaProducer<>(prop);) {
            for (int i = 0; i < 10; i++) {
                String topic = "first_topic";
                String message = "message " + i;
                String key = "id_" + i;

                ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);
                log.info("key = " + key);
                producer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata meta, Exception ex) {
                        if(ex == null){
                            log.info("Metadata: \n Topic: {} \n Offset: {} \n Partition: {} \n Timestamp: {}",
                                    meta.topic(), meta.offset(), meta.partition(), meta.timestamp());
                        }
                        else {
                            log.error("Got error during send", ex);
                        }
                    }
                }).get(); //to do sync
            }
            producer.flush();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
