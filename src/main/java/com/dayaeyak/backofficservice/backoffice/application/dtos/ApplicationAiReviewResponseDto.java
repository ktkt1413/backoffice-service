package com.dayaeyak.backofficservice.backoffice.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationAiReviewResponseDto {

    private Long applicationId;
    private String summary;
    private List<String> approvalReasons;
    private List<String> rejectionReasons;
}
