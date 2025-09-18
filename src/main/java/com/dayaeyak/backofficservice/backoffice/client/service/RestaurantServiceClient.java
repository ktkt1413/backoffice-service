package com.dayaeyak.backofficservice.backoffice.client.service;

import com.dayaeyak.backofficservice.backoffice.client.dtos.RestaurantRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="restaurants-server")
public interface RestaurantServiceClient {
    @PostMapping("/restaurants")
    void registerRestaurant(@RequestBody RestaurantRequestDto dto);
}
