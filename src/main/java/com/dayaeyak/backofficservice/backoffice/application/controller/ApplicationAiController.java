package com.dayaeyak.backofficservice.backoffice.application.controller;

import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationAiReviewResponseDto;
import com.dayaeyak.backofficservice.backoffice.application.service.ApplicationAiService;
import com.dayaeyak.backofficservice.backoffice.common.security.Action;
import com.dayaeyak.backofficservice.backoffice.user.annotation.Authorize;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/backoffice/applications")
public class ApplicationAiController {

    private final ApplicationAiService applicationAiService;

    @Authorize(action = Action.READ, resourceId = "applicationId")
    @PostMapping("/{applicationId}/ai-review")
    public ResponseEntity<ApplicationAiReviewResponseDto> reviewApplication(
            @PathVariable Long applicationId
    ) {
        return ResponseEntity.ok(applicationAiService.reviewApplication(applicationId));
    }
}
