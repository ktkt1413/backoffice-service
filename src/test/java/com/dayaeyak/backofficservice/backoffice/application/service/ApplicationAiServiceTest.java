package com.dayaeyak.backofficservice.backoffice.application.service;

import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationAiReviewResponseDto;
import com.dayaeyak.backofficservice.backoffice.application.entity.Application;
import com.dayaeyak.backofficservice.backoffice.application.repository.ApplicationRepository;
import com.dayaeyak.backofficservice.backoffice.client.dtos.AiReviewResult;
import com.dayaeyak.backofficservice.backoffice.client.service.OpenAiReviewClient;
import com.dayaeyak.backofficservice.backoffice.common.enums.ApplicationStatus;
import com.dayaeyak.backofficservice.backoffice.common.enums.BusinessType;
import com.dayaeyak.backofficservice.backoffice.common.exception.BusinessException;
import com.dayaeyak.backofficservice.backoffice.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ApplicationAiServiceTest {

    @Mock
    private ApplicationRepository repository;

    @Mock
    private OpenAiReviewClient openAiReviewClient;

    @InjectMocks
    private ApplicationAiService applicationAiService;

    @Test
    void reviewApplicationThrowsApplicationNotFoundWhenApplicationDoesNotExist() {
        given(repository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> applicationAiService.reviewApplication(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.APPLICATION_NOT_FOUND);
    }

    @Test
    void reviewApplicationReturnsAiReviewDtoWhenApplicationExists() {
        Application application = createApplication();
        AiReviewResult result = new AiReviewResult(
                "서울 강남구 소재 음식점 등록 신청입니다.",
                List.of("승인 사유 1", "승인 사유 2", "승인 사유 3"),
                List.of()
        );

        given(repository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(application));
        given(openAiReviewClient.review(application)).willReturn(result);

        ApplicationAiReviewResponseDto response = applicationAiService.reviewApplication(1L);

        assertThat(response.getApplicationId()).isEqualTo(1L);
        assertThat(response.getSummary()).isEqualTo("서울 강남구 소재 음식점 등록 신청입니다.");
        assertThat(response.getApprovalReasons()).hasSize(3);
        assertThat(response.getRejectionReasons()).isEmpty();
    }

    @Test
    void reviewApplicationReturnsOnlyRejectionReasonsWhenBothDirectionsExist() {
        Application application = createApplication();
        AiReviewResult result = new AiReviewResult(
                "서울 강남구 소재 음식점 등록 신청입니다.",
                List.of("승인 사유 1", "승인 사유 2", "승인 사유 3"),
                List.of("반려 사유 1", "반려 사유 2", "반려 사유 3")
        );

        given(repository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(application));
        given(openAiReviewClient.review(application)).willReturn(result);

        ApplicationAiReviewResponseDto response = applicationAiService.reviewApplication(1L);

        assertThat(response.getApprovalReasons()).isEmpty();
        assertThat(response.getRejectionReasons()).hasSize(3);
    }

    @Test
    void reviewApplicationWrapsOpenAiFailureAsBusinessException() {
        Application application = createApplication();

        given(repository.findByIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(application));
        given(openAiReviewClient.review(application))
                .willThrow(new BusinessException(ErrorCode.AI_REVIEW_FAILED));

        assertThatThrownBy(() -> applicationAiService.reviewApplication(1L))
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
