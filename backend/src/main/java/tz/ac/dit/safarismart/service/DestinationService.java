package tz.ac.dit.safarismart.service;

import tz.ac.dit.safarismart.dto.AttractionSummaryDto;
import tz.ac.dit.safarismart.dto.DestinationSummaryDto;
import tz.ac.dit.safarismart.entity.Attraction;
import tz.ac.dit.safarismart.entity.Destination;
import tz.ac.dit.safarismart.exception.ResourceNotFoundException;
import tz.ac.dit.safarismart.repository.AttractionRepository;
import tz.ac.dit.safarismart.repository.DestinationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DestinationService {

    private final DestinationRepository destinationRepository;
    private final AttractionRepository attractionRepository;

    public DestinationService(DestinationRepository destinationRepository,
                               AttractionRepository attractionRepository) {
        this.destinationRepository = destinationRepository;
        this.attractionRepository = attractionRepository;
    }

    public List<DestinationSummaryDto> findAll() {
        return destinationRepository.findAll().stream()
                .map(this::toSummary)
                .toList();
    }

    public DestinationSummaryDto findById(Long id) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: id=" + id));
        return toSummary(destination);
    }

    public List<AttractionSummaryDto> findAttractions(Long destinationId) {
        if (!destinationRepository.existsById(destinationId)) {
            throw new ResourceNotFoundException("Destination not found: id=" + destinationId);
        }
        List<Attraction> attractions = attractionRepository.findByDestinationIdAndActiveTrue(destinationId);
        return attractions.stream().map(this::toAttractionSummary).toList();
    }

    private DestinationSummaryDto toSummary(Destination d) {
        return new DestinationSummaryDto(
                d.getId(),
                d.getName(),
                d.getRegion().getName(),
                d.getDescription(),
                d.getLatitude(),
                d.getLongitude()
        );
    }

    private AttractionSummaryDto toAttractionSummary(Attraction a) {
        return new AttractionSummaryDto(
                a.getId(),
                a.getName(),
                a.getCategory(),
                a.getInterestTags(),
                a.getDescription(),
                a.getEntranceFeeMin(),
                a.getEntranceFeeMax(),
                a.getAvgDurationHours(),
                a.isCommunityBased(),
                a.isActive(),
                new AttractionSummaryDto.DestinationReference(
                        a.getDestination().getId(),
                        a.getDestination().getName()
                )
        );
    }
}
