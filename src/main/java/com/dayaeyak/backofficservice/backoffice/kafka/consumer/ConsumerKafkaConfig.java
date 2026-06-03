package com.dayaeyak.backofficservice.backoffice.kafka.consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
public class ConsumerKafkaConfig {
    // 현재 backoffice-service는 Kafka producer만 사용한다.
    // Consumer 설정은 추후 backoffice가 Kafka 메시지를 수신할 때 확장하기 위한 자리다.
}
