package tz.ac.dit.safarismart.dto;

import java.math.BigDecimal;

public record AttractionSummaryDto(
        Long id,
        String name,
        String category,
        String[] interestTags,
        String description,
        BigDecimal entranceFeeMin,
        BigDecimal entranceFeeMax,
        BigDecimal avgDurationHours,
        boolean communityBased,
        boolean active,
        DestinationReference destination
) {
    public record DestinationReference(Long id, String name) {
    }
}
