package com.dayaeyak.backofficservice.backoffice.client.mapper;

import com.dayaeyak.backofficservice.backoffice.application.entity.Application;
import com.dayaeyak.backofficservice.backoffice.client.dtos.RestaurantRequestDto;
import com.dayaeyak.backofficservice.backoffice.client.enums.restaurant.RestaurantType;

public class ApplicationToRestaurantMapper {
    public static RestaurantRequestDto toRestaurantRequestDto(Application application) {
        return RestaurantRequestDto.builder()
                // 신청서에서 넘어온 값
                .applicationId(application.getId())
                .name(application.getBusinessName())
                .sellerId(application.getSellerId())
                .address(application.getAddress())
                .phoneNumber(application.getContactNumber())
                .type(RestaurantType.KOREAN)
                .build();
    }
}
