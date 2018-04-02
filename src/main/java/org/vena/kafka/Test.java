package org.vena.kafka;


import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

public class Test {

    private final static String TOPIC = "my-example-topic";
    private final static String BOOTSTRAP_SERVERS = "localhost:9092";

    private static Consumer<Long, String> createConsumer() {
	final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,"KafkaExampleConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
//        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 1);


        // Create the consumer using props.
        final Consumer<Long, String> consumer = new KafkaConsumer<>(props);
        
        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(TOPIC));
        return consumer;
    }
  
    static void runConsumer() throws InterruptedException {
	System.out.println("1");
	final Consumer<Long, String> consumer = createConsumer();

        final int giveUp = 2000;
        int noRecordsCount = 0;

        while (true) {

    	    final ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);

            if (consumerRecords.count()==0) {
                noRecordsCount++;
        	System.out.println(noRecordsCount);

                if (noRecordsCount > giveUp) { 
            	    break;
            	} else {
            	    continue;
            	}
            }

            consumerRecords.forEach(record -> {
            	    System.out.printf("Consumer Record:(%d, %s, %d, %d)\n",
                    		       record.key(), record.value(),
                    		       record.partition(), record.offset());
            });

            consumer.commitAsync();
        }
        consumer.close();
        System.out.println("DONE");
    }




    public static void main(String[] args) throws Exception {
	System.out.println("Hello world!");
	runConsumer();
    }
}
