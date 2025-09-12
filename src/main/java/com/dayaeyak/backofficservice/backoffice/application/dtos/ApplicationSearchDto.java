package com.dayaeyak.backofficservice.backoffice.application.dtos;

import com.dayaeyak.backofficservice.backoffice.common.enums.BusinessType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ApplicationSearchDto {

    private Long id;
    private Long sellerId;
    private Long registrationNumber;
    private String businessName;
    private String owner;

    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdFrom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTo;


}
