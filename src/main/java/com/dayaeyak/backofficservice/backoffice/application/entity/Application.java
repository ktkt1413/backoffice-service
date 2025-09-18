package com.dayaeyak.backofficservice.backoffice.application.entity;


import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationRequestDto;
import com.dayaeyak.backofficservice.backoffice.common.enums.ApplicationStatus;
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
@Table(name = "application")
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

    private String contactNumber;

    private String email;

    private ApplicationStatus status;

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

    public static Application createApplication(ApplicationRequestDto dto, Long userId) {
        return Application.builder()
                .sellerId(userId)
                .registrationNumber(dto.getRegistrationNumber())
                .businessName(dto.getBusinessName())
                .owner(dto.getOwner())
                .businessType(dto.getBusinessType())
                .address(dto.getAddress())
                .contactNumber(dto.getContactNumber())
                .email(dto.getEmail())
                .status(ApplicationStatus.PENDING)
                .createdAt(LocalDateTime.now())
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

    // 신청서 승인
    public void approve() {
        if (this.status == ApplicationStatus.APPROVAL_REQUESTED) {
            this.status = ApplicationStatus.APPROVED;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalArgumentException("승인 요청된 신청서가 아닙니다.");
        }
    }

    // 신청서 거절
    public void reject() {
        if (this.status == ApplicationStatus.APPROVAL_REQUESTED) {
            this.status = ApplicationStatus.REJECTED;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalArgumentException("승인 요청된 신청서가 아닙니다.");
        }
    }
}
