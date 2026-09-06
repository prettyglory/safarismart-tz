package tz.ac.dit.safarismart.dto.admin;

import tz.ac.dit.safarismart.entity.TravelStyle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RestaurantRequest(
        @NotNull Long destinationId,
        @NotBlank @Size(max = 200) String name,
        String cuisineType,
        @NotNull TravelStyle priceRange
) {
}
