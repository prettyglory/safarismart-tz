package tz.ac.dit.safarismart.dto;

public record DestinationSummaryDto(
        Long id,
        String name,
        String regionName,
        String description,
        Double latitude,
        Double longitude
) {
}
