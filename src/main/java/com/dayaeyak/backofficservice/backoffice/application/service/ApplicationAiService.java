package com.dayaeyak.backofficservice.backoffice.application.service;

import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationAiReviewResponseDto;
import com.dayaeyak.backofficservice.backoffice.application.entity.Application;
import com.dayaeyak.backofficservice.backoffice.application.repository.ApplicationRepository;
import com.dayaeyak.backofficservice.backoffice.client.dtos.AiReviewResult;
import com.dayaeyak.backofficservice.backoffice.client.service.OpenAiReviewClient;
import com.dayaeyak.backofficservice.backoffice.common.exception.BusinessException;
import com.dayaeyak.backofficservice.backoffice.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationAiService {

    private final ApplicationRepository repository;
    private final OpenAiReviewClient openAiReviewClient;

    @Transactional(readOnly = true)
    public ApplicationAiReviewResponseDto reviewApplication(Long applicationId) {
        Application application = repository.findByIdAndDeletedAtIsNull(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        AiReviewResult result = openAiReviewClient.review(application);
        AiReviewResult singleDirectionResult = selectSingleDirection(result);

        return ApplicationAiReviewResponseDto.builder()
                .applicationId(application.getId())
                .summary(singleDirectionResult.summary())
                .approvalReasons(singleDirectionResult.approvalReasons())
                .rejectionReasons(singleDirectionResult.rejectionReasons())
                .build();
    }

    private AiReviewResult selectSingleDirection(AiReviewResult result) {
        if (hasAnyReason(result.rejectionReasons())) {
            return new AiReviewResult(result.summary(), List.of(), result.rejectionReasons());
        }
        if (hasAnyReason(result.approvalReasons())) {
            return new AiReviewResult(result.summary(), result.approvalReasons(), List.of());
        }
        return new AiReviewResult(result.summary(), List.of(), List.of());
    }

    private boolean hasAnyReason(List<String> reasons) {
        return reasons != null && reasons.stream().anyMatch(reason -> reason != null && !reason.isBlank());
    }
}
