package com.dayaeyak.backofficservice.backoffice.application.entity;


import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationRequestDto;
import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationResponseDto;
import com.dayaeyak.backofficservice.backoffice.common.enums.BusinessType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applicaion")
@SQLDelete(sql = "UPDATE application SET deleted_at= NOW() WHERE id =?")
@Where(clause = "deleted_at IS Null")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;

    private Long registrationNumber;

    private String businessName;

    private String owner;

    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    private String address;

    private Long contactNumber;

    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @CreatedDate
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    //생성

    public Application createApplication(ApplicationRequestDto dto, Long userId) {
        return Application.builder()
                .sellerId(this.sellerId)
                .registrationNumber(this.registrationNumber)
                .businessName(this.businessName)
                .owner(this.owner)
                .businessType(this.businessType)
                .address(this.address)
                .contactNumber(this.contactNumber)
                .email(this.email)
                .createdAt(this.createdAt)
                .build();
    }

    //수정
    public void updateApplication(ApplicationRequestDto dto) {
        this.registrationNumber = dto.getRegistrationNumber();
        this.businessName = dto.getBusinessName();
        this.owner = dto.getOwner();
        this.businessType = dto.getBusinessType();
        this.address = dto.getAddress();
        this.contactNumber = dto.getContactNumber();
        this.email = dto.getEmail();
        this.updatedAt = LocalDateTime.now();
    }

    //소프트 삭제
    public void deleteApplication() {
        this.deletedAt = LocalDateTime.now();
    }

    public ApplicationResponseDto toResponseDto() {
        ApplicationResponseDto dto = new ApplicationResponseDto();
        dto.setId(this.id);
        dto.setSellerId(this.sellerId);
        dto.setRegistrationNumber(this.registrationNumber);
        dto.setBusinessName(this.businessName);
        dto.setOwner(this.owner);
        dto.setBusinessType(this.businessType);
        dto.setAddress(this.address);
        dto.setContactNumber(this.contactNumber);
        dto.setEmail(this.email);
        dto.setCreatedAt(this.createdAt);
        dto.setUpdatedAt(this.updatedAt);

        return dto;
    }

}
