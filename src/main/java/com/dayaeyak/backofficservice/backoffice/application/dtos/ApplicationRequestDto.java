package com.dayaeyak.backofficservice.backoffice.application.dtos;


import com.dayaeyak.backofficservice.backoffice.common.enums.BusinessType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationRequestDto {

    @NotNull(message = "사업자 등록번호는 필수입니다.")
    private Long registrationNumber;

    @NotBlank(message = "사업장명은 필수입니다.")
    private String businessName;

    @NotBlank(message = "대표자명은 필수입니다.")
    private String owner;

    private Long sellerId;

    @NotNull(message = "업종은 필수입니다.")
    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @NotBlank(message = "연락처는 필수입니다.")
    private String contactNumber;

    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

}
