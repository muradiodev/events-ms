package com.eventeam.events.kafka;


import com.eventeam.events.client.internal.NotficationDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventKafka {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.template.send-notification-topic}")
    private String dataTopic;

    public void sendData(NotficationDto value) {
        log.info("Outgoing data: {}", value);
        kafkaTemplate.send(dataTopic, value);
    }
}
