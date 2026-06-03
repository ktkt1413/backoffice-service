package com.dayaeyak.backofficservice.backoffice.client.dtos;

import java.util.List;

public record AiReviewResult(
        String summary,
        List<String> approvalReasons,
        List<String> rejectionReasons
) {
}
