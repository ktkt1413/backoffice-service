package com.dayaeyak.backofficservice.backoffice.kafka.producer;



import com.dayaeyak.backofficservice.backoffice.kafka.producer.dtos.BackofficeRegisterDto;
import com.dayaeyak.backofficservice.backoffice.kafka.producer.dtos.ServiceRegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProducerService {

    private final KafkaTemplate<String, ServiceRegisterRequestDto> kafkaTemplateSRR;
    private final KafkaTemplate<String, BackofficeRegisterDto> kafkaTemplateBOR;

    // 음식점/공연/전시회 등록 결과 전송
    public void sendRegisterResult(String topic, String key, ServiceRegisterRequestDto dto) {
        kafkaTemplateSRR.send(topic, key, dto);
    }

    //백오피스에서 등록 요청 전송
    public void sendBackOfficeRegister(String topic, String key, BackofficeRegisterDto  dto) {
        kafkaTemplateBOR.send(topic, key, dto);
    }


}
