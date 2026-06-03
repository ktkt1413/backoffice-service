package com.dayaeyak.backofficservice.backoffice.client.service;

import com.dayaeyak.backofficservice.backoffice.application.entity.Application;
import com.dayaeyak.backofficservice.backoffice.client.dtos.AiReviewResult;
import com.dayaeyak.backofficservice.backoffice.common.exception.BusinessException;
import com.dayaeyak.backofficservice.backoffice.common.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiReviewClientImpl implements OpenAiReviewClient {

    private static final String OPENAI_CHAT_COMPLETIONS_URL = "https://api.openai.com/v1/chat/completions";

    private final ObjectMapper objectMapper;

    @Value("${openai.api-key:}")
    private String apiKey;

    @Value("${openai.model:gpt-5.4-mini}")
    private String model;

    @Override
    public AiReviewResult review(Application application) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessException(ErrorCode.AI_REVIEW_FAILED, "OpenAI API key가 설정되지 않았습니다.");
        }

        try {
            OpenAiChatResponse response = RestClient.create()
                    .post()
                    .uri(OPENAI_CHAT_COMPLETIONS_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createRequest(application))
                    .retrieve()
                    .body(OpenAiChatResponse.class);

            String content = extractContent(response);
            return parseResultOrFallback(application, content);
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.AI_REVIEW_FAILED, "OpenAI API 호출에 실패했습니다.");
        }
    }

    private OpenAiChatRequest createRequest(Application application) {
        return new OpenAiChatRequest(
                model,
                List.of(
                        new OpenAiMessage("system", createSystemPrompt()),
                        new OpenAiMessage("user", createUserPrompt(application))
                ),
                Map.of("type", "json_object")
        );
    }

    private String createSystemPrompt() {
        return """
                당신은 백오피스 관리자의 신청서 검토를 돕는 보조 AI입니다.
                AI는 최종 승인 또는 반려 판단을 하지 않습니다.
                관리자가 참고할 수 있도록 신청 정보를 요약하고, 승인 사유 후보 또는 반려 사유 후보 중 하나만 제안합니다.
                신청 정보에 주요 누락 또는 보완 필요 사항이 있으면 rejectionReasons만 작성하고 approvalReasons는 빈 배열로 두세요.
                주요 누락 사항이 명확하지 않으면 approvalReasons만 작성하고 rejectionReasons는 빈 배열로 두세요.
                확정적인 표현을 피하고, "확인이 필요합니다", "검토할 수 있습니다", "보완 요청을 고려할 수 있습니다"처럼 보조적인 표현을 사용하세요.
                모든 응답은 한국어로 작성하세요.
                반드시 아래 JSON 형식만 응답하세요.
                {
                  "summary": "신청서 요약 1개",
                  "approvalReasons": ["승인 사유 후보 1", "승인 사유 후보 2", "승인 사유 후보 3"],
                  "rejectionReasons": []
                }
                또는
                {
                  "summary": "신청서 요약 1개",
                  "approvalReasons": [],
                  "rejectionReasons": ["반려 사유 후보 1", "반려 사유 후보 2", "반려 사유 후보 3"]
                }
                """;
    }

    private String createUserPrompt(Application application) {
        return """
                다음 신청서를 검토 보조 관점에서 분석하세요.

                신청서 ID: %d
                판매자 ID: %s
                사업자 등록번호: %s
                사업장명: %s
                대표자명: %s
                사업 유형: %s
                주소: %s
                연락처: %s
                이메일: %s
                현재 상태: %s
                """.formatted(
                application.getId(),
                valueOf(application.getSellerId()),
                valueOf(application.getRegistrationNumber()),
                valueOf(application.getBusinessName()),
                valueOf(application.getOwner()),
                valueOf(application.getBusinessType()),
                valueOf(application.getAddress()),
                valueOf(application.getContactNumber()),
                valueOf(application.getEmail()),
                valueOf(application.getStatus())
        );
    }

    private String extractContent(OpenAiChatResponse response) {
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new BusinessException(ErrorCode.AI_REVIEW_FAILED, "OpenAI 응답이 비어 있습니다.");
        }

        OpenAiMessage message = response.choices().get(0).message();
        if (message == null || message.content() == null || message.content().isBlank()) {
            throw new BusinessException(ErrorCode.AI_REVIEW_FAILED, "OpenAI 응답 내용이 비어 있습니다.");
        }
        return message.content();
    }

    private AiReviewResult parseResultOrFallback(Application application, String content) {
        try {
            JsonNode root = objectMapper.readTree(content);
            String summary = root.path("summary").asText(null);
            List<String> approvalReasons = readStringList(root.path("approvalReasons"));
            List<String> rejectionReasons = readStringList(root.path("rejectionReasons"));

            if (summary == null) {
                return fallback(application);
            }

            return selectSingleDirection(application, summary, approvalReasons, rejectionReasons);
        } catch (JsonProcessingException e) {
            return fallback(application);
        }
    }

    private List<String> readStringList(JsonNode node) {
        if (!node.isArray()) {
            return List.of();
        }
        return objectMapper.convertValue(
                node,
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
        );
    }

    private AiReviewResult fallback(Application application) {
        List<String> rejectionReasons = createFallbackRejectionReasons(application);
        if (hasAnyReason(rejectionReasons)) {
            return new AiReviewResult(
                    createFallbackSummary(application),
                    List.of(),
                    normalizeReasons(rejectionReasons)
            );
        }

        return new AiReviewResult(
                createFallbackSummary(application),
                normalizeReasons(createFallbackApprovalReasons(application)),
                List.of()
        );
    }

    private AiReviewResult selectSingleDirection(
            Application application,
            String summary,
            List<String> approvalReasons,
            List<String> rejectionReasons
    ) {
        List<String> fallbackRejectionReasons = createFallbackRejectionReasons(application);
        if (hasAnyReason(fallbackRejectionReasons)) {
            List<String> reasonsToUse = hasAnyReason(rejectionReasons)
                    ? rejectionReasons
                    : fallbackRejectionReasons;
            return new AiReviewResult(summary, List.of(), normalizeReasons(reasonsToUse));
        }

        List<String> reasonsToUse = hasAnyReason(approvalReasons)
                ? approvalReasons
                : createFallbackApprovalReasons(application);
        return new AiReviewResult(summary, normalizeReasons(reasonsToUse), List.of());
    }

    private String createFallbackSummary(Application application) {
        return "%s 소재의 %s 등록 신청입니다. 사업장명은 %s이며, 입력된 신청 정보를 기준으로 관리자 검토가 필요합니다."
                .formatted(
                        valueOf(application.getAddress()),
                        valueOf(application.getBusinessType()),
                        valueOf(application.getBusinessName())
                );
    }

    private List<String> createFallbackApprovalReasons(Application application) {
        List<String> reasons = new ArrayList<>();

        if (hasText(application.getBusinessName())) {
            reasons.add("사업장명이 입력되어 신청 대상 사업장을 확인할 수 있습니다.");
        }
        if (hasText(application.getOwner())) {
            reasons.add("대표자명이 입력되어 신청 주체를 검토할 수 있습니다.");
        }
        if (application.getBusinessType() != null) {
            reasons.add("사업 유형이 입력되어 등록 대상 서비스를 분류할 수 있습니다.");
        }
        if (application.getRegistrationNumber() != null) {
            reasons.add("사업자 등록번호가 입력되어 사업자 정보 확인을 진행할 수 있습니다.");
        }
        if (hasText(application.getAddress())) {
            reasons.add("주소 정보가 입력되어 사업장 위치를 검토할 수 있습니다.");
        }
        if (hasText(application.getContactNumber())) {
            reasons.add("연락처가 입력되어 추가 확인 또는 보완 요청이 가능합니다.");
        }

        return reasons;
    }

    private List<String> createFallbackRejectionReasons(Application application) {
        List<String> reasons = new ArrayList<>();

        if (application.getRegistrationNumber() == null) {
            reasons.add("사업자 등록 정보 확인이 필요합니다.");
        }
        if (!hasText(application.getBusinessName())) {
            reasons.add("사업장명 정보 확인이 필요합니다.");
        }
        if (!hasText(application.getOwner())) {
            reasons.add("대표자명 정보 확인이 필요합니다.");
        }
        if (application.getBusinessType() == null) {
            reasons.add("사업 유형 정보 확인이 필요합니다.");
        }
        if (!hasText(application.getAddress())) {
            reasons.add("주소 정보 확인이 필요합니다.");
        }
        if (!hasText(application.getContactNumber())) {
            reasons.add("연락처 정보 확인이 필요합니다.");
        }
        if (!hasText(application.getEmail())) {
            reasons.add("이메일 정보 확인이 필요합니다.");
        }

        return reasons;
    }

    private List<String> normalizeReasons(List<String> reasons) {
        List<String> normalized = new ArrayList<>(reasons.stream()
                .filter(this::hasText)
                .toList());
        while (normalized.size() < 3) {
            normalized.add("");
        }
        return normalized.stream().limit(3).toList();
    }

    private boolean hasAnyReason(List<String> reasons) {
        return reasons != null && reasons.stream().anyMatch(this::hasText);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String valueOf(Object value) {
        return value == null ? "미입력" : String.valueOf(value);
    }

    private record OpenAiChatRequest(
            String model,
            List<OpenAiMessage> messages,
            Map<String, String> response_format
    ) {
    }

    private record OpenAiMessage(String role, String content) {
    }

    private record OpenAiChatResponse(List<OpenAiChoice> choices) {
    }

    private record OpenAiChoice(OpenAiMessage message) {
    }
}
