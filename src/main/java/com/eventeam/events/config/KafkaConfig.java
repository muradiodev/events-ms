package com.eventeam.events.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;

public class KafkaConfig {

    @EnableKafka
    @Configuration
    public class Config { @Bean
    public ByteArrayJsonMessageConverter jsonConverter() { return new ByteArrayJsonMessageConverter(); } }
}
