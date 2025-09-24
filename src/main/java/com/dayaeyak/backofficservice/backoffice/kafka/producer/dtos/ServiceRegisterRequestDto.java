package com.dayaeyak.backofficservice.backoffice.kafka.producer.dtos;


import com.dayaeyak.backofficservice.backoffice.kafka.producer.enums.ServiceType;
import com.dayaeyak.backofficservice.backoffice.kafka.producer.enums.Status;

public record ServiceRegisterRequestDto(
        Long userId,
        ServiceType serviceType,
        Long serviceId,
        String userName,
        Status status

) {
}
