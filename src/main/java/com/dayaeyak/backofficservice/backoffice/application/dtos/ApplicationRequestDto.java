package com.dayaeyak.backofficservice.backoffice.application.dtos;


import com.dayaeyak.backofficservice.backoffice.common.enums.BusinessType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationRequestDto {

    private Long registrationNumber;

    private String businessName;

    private String owner;

    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    private String address;

    private String contactNumber;

    private String email;

}
