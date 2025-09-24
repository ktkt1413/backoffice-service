package com.dayaeyak.backofficservice.backoffice.client.mapper;

import com.dayaeyak.backofficservice.backoffice.application.entity.Application;
import com.dayaeyak.backofficservice.backoffice.client.dtos.RestaurantRequestDto;
import com.dayaeyak.backofficservice.backoffice.client.enums.restaurant.ActivationStatus;
import com.dayaeyak.backofficservice.backoffice.client.enums.restaurant.ClosedDays;
import com.dayaeyak.backofficservice.backoffice.client.enums.restaurant.RestaurantType;
import com.dayaeyak.backofficservice.backoffice.client.enums.restaurant.WaitingStatus;

import java.time.LocalTime;

public class ApplicationToRestaurantMapper {
    public static RestaurantRequestDto toRestaurantRequestDto(Application application) {
        return RestaurantRequestDto.builder()
                // 신청서에서 넘어온 값
                .applicationId(application.getId())
                .name(application.getBusinessName())
                .sellerId(application.getSellerId())
                .address(application.getAddress())
                .phoneNumber(application.getContactNumber())

                // 신청서에는 없는 값 → 예시/기본값 세팅
                .closedDay(ClosedDays.SUNDAY)                // 기본값: 휴무일 없음
                .openTime(LocalTime.parse("09:00"))  // LocalTime 그대로
                .closeTime(LocalTime.parse("21:00"))      // 기본값: 21:00 마감
                .capacity(50)                              // 기본값: 50명 수용
                .city("서울")                               // 기본값: "서울"
                .type(RestaurantType.KOREAN)               // 기본값: 한식 (예시)
                .build();
    }
}
