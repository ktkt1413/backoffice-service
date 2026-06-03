package com.dayaeyak.backofficservice.backoffice.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationStatus {
    PENDING("대기"),
    APPROVING("승인 처리 중"),
    APPROVED("승인"),
    APPROVAL_FAILED("승인 처리 실패"),
    APPROVAL_REQUESTED("승인 신청됨"),
    REJECTED("신청서 거절");

    private final String description;
}
