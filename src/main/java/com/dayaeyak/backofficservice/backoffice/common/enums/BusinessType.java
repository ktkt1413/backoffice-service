package com.dayaeyak.backofficservice.backoffice.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum BusinessType {
    PERFORMANCE("공연"),
    EXHIBITION("전시회"),
    RESTAURANT("음식점"),
    ;
    private final String type;

    public static BusinessType of(String type) {
        return Stream.of(BusinessType.values())
                .filter(businessType -> businessType.type.equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

}
