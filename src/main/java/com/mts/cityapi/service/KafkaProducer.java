package com.mts.cityapi.service;

import com.mts.dto.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    @Value("${kafka.producer.topic}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, DomainEvent> kafkaTemplate;

    public void sendMessage(String key, DomainEvent value){
        try {
            this.kafkaTemplate.send(topicName, key, value);

            logger.info(String.format("***** MyPublisher sent message: key=%s and value=%s *****", key, value));

            // todo: write business logic to act on this event
            // in this case, we are just sending message
        }
        catch (Exception exception)
        {
            logger.error("Error occurred in sendMessage():" + exception.toString());
        }
    }
}