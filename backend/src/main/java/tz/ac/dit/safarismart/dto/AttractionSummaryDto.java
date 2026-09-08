package tz.ac.dit.safarismart.dto;

import java.math.BigDecimal;

public record AttractionSummaryDto(
        Long id,
        DestinationReference destination,
        String name,
        String category,
        String[] interestTags,
        String description,
        BigDecimal entranceFeeMin,
        BigDecimal entranceFeeMax,
        BigDecimal avgDurationHours,
        boolean communityBased,
        boolean active
) {
    public record DestinationReference(Long id, String name) {
    }
}
