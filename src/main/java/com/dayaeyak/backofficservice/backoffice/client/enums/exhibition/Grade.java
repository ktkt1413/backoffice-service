package com.dayaeyak.backofficservice.backoffice.client.enums.exhibition;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Grade {

    ALL("ALL"),
    R15_PLUS("R15+"),
    R18_PLUS("R18+"),
    ;

    private final String value;

    @JsonCreator
    public static Grade of(String value) {
        return Stream.of(values())
                .filter(m -> m.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow();
    }
}
