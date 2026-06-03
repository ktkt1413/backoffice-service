package com.dayaeyak.backofficservice.backoffice.client.service;

import com.dayaeyak.backofficservice.backoffice.application.entity.Application;
import com.dayaeyak.backofficservice.backoffice.client.dtos.AiReviewResult;

public interface OpenAiReviewClient {
    AiReviewResult review(Application application);
}
