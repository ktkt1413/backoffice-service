package com.dayaeyak.backofficservice.backoffice.client.service;

import com.dayaeyak.backofficservice.backoffice.application.entity.Application;
import com.dayaeyak.backofficservice.backoffice.common.enums.ApplicationStatus;
import com.dayaeyak.backofficservice.backoffice.common.enums.BusinessType;
import com.dayaeyak.backofficservice.backoffice.common.exception.BusinessException;
import com.dayaeyak.backofficservice.backoffice.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OpenAiReviewClientImplTest {

    @Test
    void reviewThrowsAiReviewFailedWhenApiKeyIsBlank() {
        OpenAiReviewClientImpl client = new OpenAiReviewClientImpl(new ObjectMapper());
        ReflectionTestUtils.setField(client, "apiKey", "");
        ReflectionTestUtils.setField(client, "model", "gpt-5.4-mini");

        assertThatThrownBy(() -> client.review(createApplication()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.AI_REVIEW_FAILED);
    }

    private Application createApplication() {
        return Application.builder()
                .id(1L)
                .sellerId(10L)
                .registrationNumber(1234567890L)
                .businessName("강남 한식당")
                .owner("홍길동")
                .businessType(BusinessType.RESTAURANT)
                .address("서울 강남구")
                .contactNumber("010-1234-5678")
                .email("owner@example.com")
                .status(ApplicationStatus.APPROVAL_REQUESTED)
                .build();
    }
}
