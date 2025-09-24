package com.dayaeyak.backofficservice.backoffice.client.service;

import com.dayaeyak.backofficservice.backoffice.client.dtos.ExhibitionCreateRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name="exhibition-service")
public interface ExhibitionServiceClient {
    @PostMapping("/internal/exhibitions")
    void registerExhibition(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role,
            @RequestBody ExhibitionCreateRequestDto dto
    );
}
