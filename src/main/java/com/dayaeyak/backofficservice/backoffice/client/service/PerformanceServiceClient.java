package com.dayaeyak.backofficservice.backoffice.client.service;

import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationResponseDto;
import com.dayaeyak.backofficservice.backoffice.client.dtos.CreatePerformanceRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "performance-server")
public interface PerformanceServiceClient {

    @PostMapping("/performances")
    void registerPerformance(@RequestBody CreatePerformanceRequestDto dto);
}
