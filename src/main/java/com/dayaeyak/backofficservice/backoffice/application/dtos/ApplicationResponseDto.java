package com.dayaeyak.backofficservice.backoffice.application.dtos;


import com.dayaeyak.backofficservice.backoffice.application.entity.Application;
import com.dayaeyak.backofficservice.backoffice.common.enums.ApplicationStatus;
import com.dayaeyak.backofficservice.backoffice.common.enums.BusinessType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponseDto {

    private Long id;
    private Long sellerId;
    private Long registrationNumber;
    private String businessName;
    private String owner;

    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    private String address;
    private String contactNumber;
    private String email;
    private ApplicationStatus status;

    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "HH:mm")
    private LocalDateTime updatedAt;

    public static ApplicationResponseDto from(Application application) {
        return ApplicationResponseDto.builder()
                .id(application.getId())
                .sellerId(application.getSellerId())
                .registrationNumber(application.getRegistrationNumber())
                .businessName(application.getBusinessName())
                .owner(application.getOwner())
                .businessType(application.getBusinessType())
                .address(application.getAddress())
                .contactNumber(application.getContactNumber())
                .email(application.getEmail())
                .status(application.getStatus())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }

}
