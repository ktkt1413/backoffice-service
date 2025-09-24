package com.dayaeyak.backofficservice.backoffice.kafka.producer;

import com.dayaeyak.backofficservice.backoffice.kafka.producer.dtos.BackofficeRegisterDto;
import com.dayaeyak.backofficservice.backoffice.kafka.producer.dtos.ServiceRegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProducerController {

    private final ProducerService producerService;

    @PostMapping("/send-register/topic")
    public String sendRegisterMessage(@RequestParam("topic") String topic,
                                   @RequestParam("key") String key,
                                   @RequestBody ServiceRegisterRequestDto dto) {
        producerService.sendRegisterResult(topic, key, dto);
        return "ServiceRegisterRequestDto message sent to Kafka topic: " + topic;
    }


    @PostMapping("/send-backoffice")
    public String sendBackofficeMessage(@RequestParam("topic") String topic,
                                   @RequestParam("key") String key,
                                   @RequestBody BackofficeRegisterDto dto) {
        producerService.sendBackOfficeRegister(topic, key, dto);
        return "BackofficeRegisterDto message sent to Kafka topic: " + topic;
    }

}
