package com.mts.cityapi.service;

import com.mts.cityapi.events.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    private OutboundApi outboundApi;

    @KafkaListener(topics = "${kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void receiveMessage(
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String timestamp,
            @Header(KafkaHeaders.OFFSET) String offset,
            DomainEvent value) {
        try {

            logger.info(String.format("***** MyConsumer consumed message: key=%s, topic=%s, timestamp=%s, offset=%s, value=%s *****", key, topic, timestamp, offset, value));

            // in this case, we are publishing message to another topic, so that next microservice kicks-in
            // check if message is for city api from incoming message
            if(value.getDetailType().toLowerCase().equals("city")) // this message is meant for city api
            {
                logger.info("this message is meant for city api!");

                String city = this.outboundApi.getRandomCity();
                this.outboundApi.publishMessage(city);
            }
            else
            {
                logger.info("this message is NOT for city api");
            }
        }
        catch (Exception exception)
        {
            logger.error("Error occurred in receiveMessage():" + exception.toString());
        }
    }
}