package pro.bolshakov.edu.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyAppConsumer {
    private final static Logger log = LoggerFactory.getLogger(MyAppConsumer.class);

    public static final String FIRST_TOPIC = "first_topic";

    private static Properties getConsumerProperties() {
        Properties prop = new Properties();
        prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        prop.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        prop.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "first_group");
        prop.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return prop;
    }

    public static void main(String[] args) {

//        simpleConsumer();


        ExecutorService executorService = Executors.newFixedThreadPool(3);
        /*executorService.execute(new Runnable() {
            @Override
            public void run() {
                simpleConsumerWithGroup("group1");
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                simpleConsumerWithGroup("group1");
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                simpleConsumerWithGroup("group1");
            }
        });
*/
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                consumerAssignSeek("notNeed");
            }
        });

    }

    private static void simpleConsumer() {
        try(KafkaConsumer<String, String> consumer = new KafkaConsumer<>(getConsumerProperties())){

            consumer.subscribe(Arrays.asList(FIRST_TOPIC));

            while(true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                for (ConsumerRecord<String, String> record : records) {
                    log.info("Key -> {} \n Value -> {}", record.key(), record.value());
                    log.info("Partition: {} \n Offset: {}", record.partition(), record.offset());
                }

                if(1 == 0){
                    break;
                }
            }

        }
    }

    private static void simpleConsumerWithGroup(String groupName) {
        Properties currentProps = getConsumerProperties();
        currentProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupName);
        try(KafkaConsumer<String, String> consumer = new KafkaConsumer<>(currentProps)){

            consumer.subscribe(Arrays.asList(FIRST_TOPIC));

            while(true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                for (ConsumerRecord<String, String> record : records) {
                    log.info("Group name: {} \n Key -> {} \n Value -> {} \n Partition: {} \n Offset: {}",
                            groupName, record.key(), record.value(), record.partition(), record.offset());
                }

                if(1 == 0){
                    break;
                }
            }

        }
    }

    private static void consumerAssignSeek(String groupName){
        Properties currentProps = getConsumerProperties();
//        currentProps.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupName);

        TopicPartition partitionToReadFrom = new TopicPartition(FIRST_TOPIC, 0);
        long offsetToReadFrom = 15L;

        try(KafkaConsumer<String, String> consumer = new KafkaConsumer<>(currentProps)){

//            consumer.subscribe(Arrays.asList(FIRST_TOPIC));
            //assign to topic and partition
            consumer.assign(Collections.singleton(partitionToReadFrom));
            //set point
            consumer.seek(partitionToReadFrom, offsetToReadFrom);

            while(true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                for (ConsumerRecord<String, String> record : records) {
                    log.info("Group name: {} \n Key -> {} \n Value -> {} \n Partition: {} \n Offset: {}",
                            groupName, record.key(), record.value(), record.partition(), record.offset());
                }

                if(1 == 0){
                    break;
                }
            }

        }

    }

}
