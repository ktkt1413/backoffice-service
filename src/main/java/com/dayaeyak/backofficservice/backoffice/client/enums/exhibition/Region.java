package com.dayaeyak.backofficservice.backoffice.client.enums.exhibition;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Region {

    SEOUL("서울"),
    INCHEON("인천"),
    GYEONGGI("경기"),
    GANGWON("강원"),
    CHUNGCHEONG("충청"),
    DAEJEON("대전"),
    JEOLLA("전라"),
    GWANGJU("광주"),
    GYEONGSANG("경상"),
    DAEGU("대구"),
    ULSAN("울산"),
    BUSAN("부산"),
    JEJU("제주"),
    ;

    private final String name;

    @JsonCreator
    public static Region of(String name) {
        return Stream.of(Region.values())
                .filter(region -> region.name.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow();
    }
}