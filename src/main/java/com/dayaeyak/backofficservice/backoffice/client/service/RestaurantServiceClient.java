package com.dayaeyak.backofficservice.backoffice.client.service;

import com.dayaeyak.backofficservice.backoffice.client.dtos.RestaurantRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name="restaurants-service", url = "http://localhost:8080")
public interface RestaurantServiceClient {
    @PostMapping("/restaurants")
    void registerRestaurant(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role,
            @RequestBody RestaurantRequestDto dto
    );
}
