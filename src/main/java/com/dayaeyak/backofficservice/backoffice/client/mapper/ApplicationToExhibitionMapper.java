package com.dayaeyak.backofficservice.backoffice.client.mapper;

import com.dayaeyak.backofficservice.backoffice.application.entity.Application;
import com.dayaeyak.backofficservice.backoffice.client.dtos.ExhibitionCreateArtistRequestDto;
import com.dayaeyak.backofficservice.backoffice.client.dtos.ExhibitionCreateRequestDto;
import com.dayaeyak.backofficservice.backoffice.client.enums.exhibition.Region;
import com.dayaeyak.backofficservice.backoffice.client.enums.exhibition.Grade;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ApplicationToExhibitionMapper {
    public static ExhibitionCreateRequestDto toExhibitionRequestDto(Application application) {
        return ExhibitionCreateRequestDto.builder()
                // Application 에서 가져오는 값
                .sellerId(application.getSellerId())
                .name(application.getBusinessName())
                .place("서울 전시 센터")                  // 기본값
                .address(application.getAddress())
                .region(Region.SEOUL)                   // 기본값 (예시: 서울)
                .grade(Grade.ALL)                       // 기본값 (예시: 전체 관람가)

                // 전시회 일정 관련 기본값
                .startDate(LocalDate.now().plusDays(100)) // 기본값: 100일 후 시작
                .endDate(LocalDate.now().plusMonths(100)) // 기본값: 100 뒤 종료
                .startTime(LocalTime.of(10, 0))         // 기본값: 오전 10시 시작
                .endTime(LocalTime.of(18, 0))           // 기본값: 오후 6시 종료

                // 티켓팅 관련 기본값
                .ticketOpenedAt(LocalDateTime.now().plusDays(100))   // 기본값: 100일 뒤부터 오픈
                .ticketClosedAt(LocalDateTime.now().plusMonths(100)) // 기본값: 종료일과 동일

                // 가격은 신청서에 없으니 기본값 세팅
                .price(10000)                           // 기본값: 10,000원

                // 참여 작가 리스트 예시값
                .artists(List.of(
                        new ExhibitionCreateArtistRequestDto("홍길동"),
                        new ExhibitionCreateArtistRequestDto("이몽룡"),
                        new ExhibitionCreateArtistRequestDto("성춘향")
                ))
                .build();
    }
}
