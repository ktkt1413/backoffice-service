package com.dayaeyak.backofficservice.backoffice.client.mapper;

import com.dayaeyak.backofficservice.backoffice.application.entity.Application;
import com.dayaeyak.backofficservice.backoffice.client.dtos.CreatePerformanceRequestDto;
import com.dayaeyak.backofficservice.backoffice.client.enums.performance.Grade;
import com.dayaeyak.backofficservice.backoffice.client.enums.performance.Type;


import java.time.LocalDate;
import java.time.LocalDateTime;

public class ApplicationToPerformanceMapper {
    public static CreatePerformanceRequestDto toPerformanceRequestDto(Application application) {
        return CreatePerformanceRequestDto.builder()
                // 신청서에서 넘어온 값
                .sellerId(application.getSellerId())
                .performanceName(application.getBusinessName())
                .hallId(1L)                               // 기본값 예시
                .castList("홍길동,김철수,아이유")          // 예시 출연진
                .description("기본 공연 설명입니다.")       // 예시 설명
                .type(Type.MUSICAL)                        // 기본 공연 타입
                .grade(Grade.ALL)                           // 기본 관람 가능 연령
                .startDate(LocalDate.now().plusDays(100))    // 예시 시작일
                .endDate(LocalDate.now().plusMonths(100))    // 예시 종료일
                .ticketOpenAt(LocalDateTime.now().plusDays(100).withHour(0).withMinute(0).withSecond(0))
                .ticketCloseAt(LocalDateTime.now().plusDays(100).withHour(23).withMinute(59).withSecond(59))
                .isActivated(true)                         // 기본 활성화 여부
                .build();
    }
}
