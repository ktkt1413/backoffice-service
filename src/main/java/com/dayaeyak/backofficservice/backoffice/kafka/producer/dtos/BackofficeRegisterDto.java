package com.dayaeyak.backofficservice.backoffice.kafka.producer.dtos;

import com.dayaeyak.backofficservice.backoffice.kafka.producer.enums.ServiceType;

public record BackofficeRegisterDto(
        Long userId,
        ServiceType serviceType,
        Long serviceId,
        ServiceType requestServiceType,
        String requestServiceName,
        String requestUserName
) {
}
