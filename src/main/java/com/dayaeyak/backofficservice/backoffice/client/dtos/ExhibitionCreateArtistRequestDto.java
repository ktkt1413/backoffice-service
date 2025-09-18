package com.dayaeyak.backofficservice.backoffice.client.dtos;

import jakarta.validation.constraints.NotBlank;

public record ExhibitionCreateArtistRequestDto(
        @NotBlank
        String name
) {
}
