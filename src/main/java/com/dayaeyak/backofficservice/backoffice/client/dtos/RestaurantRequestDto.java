package com.dayaeyak.backofficservice.backoffice.client.dtos;

import com.dayaeyak.backofficservice.backoffice.client.enums.restaurant.ActivationStatus;
import com.dayaeyak.backofficservice.backoffice.client.enums.restaurant.ClosedDays;
import com.dayaeyak.backofficservice.backoffice.client.enums.restaurant.RestaurantType;
import com.dayaeyak.backofficservice.backoffice.client.enums.restaurant.WaitingStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantRequestDto {

    private Long applicationId;
    private String name;
    private Long sellerId;
    private String address;
    private String phoneNumber;
    private ClosedDays closedDay;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime openTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeTime;

    private RestaurantType type;
    private int capacity;
    private ActivationStatus isActivation;
    private String city;
    private WaitingStatus waitingActivation;
}
