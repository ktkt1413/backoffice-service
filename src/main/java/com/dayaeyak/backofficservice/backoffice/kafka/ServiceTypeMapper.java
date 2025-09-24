package com.dayaeyak.backofficservice.backoffice.kafka;

import com.dayaeyak.backofficservice.backoffice.common.enums.BusinessType;
import com.dayaeyak.backofficservice.backoffice.kafka.producer.enums.ServiceType;

public class ServiceTypeMapper {
    public static ServiceType fromBusinessType(BusinessType businessType) {
        return switch (businessType){
            case EXHIBITION -> ServiceType.EXHIBITION;
            case RESTAURANT ->  ServiceType.RESTAURANT;
            case PERFORMANCE ->  ServiceType.PERFORMANCE;
        };
    }
}
