package tz.ac.dit.safarismart.service.admin;

import tz.ac.dit.safarismart.dto.admin.AttractionRequest;
import tz.ac.dit.safarismart.entity.Attraction;
import tz.ac.dit.safarismart.entity.Destination;
import tz.ac.dit.safarismart.exception.ResourceNotFoundException;
import tz.ac.dit.safarismart.repository.AttractionRepository;
import tz.ac.dit.safarismart.repository.DestinationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminAttractionService {

    private final AttractionRepository attractionRepository;
    private final DestinationRepository destinationRepository;

    public AdminAttractionService(AttractionRepository attractionRepository,
                                   DestinationRepository destinationRepository) {
        this.attractionRepository = attractionRepository;
        this.destinationRepository = destinationRepository;
    }

    @Transactional(readOnly = true)
    public List<Attraction> findAll() {
        return attractionRepository.findAll();
    }

    public Attraction create(AttractionRequest request) {
        validateFeeRange(request);
        Destination destination = destinationRepository.findById(request.destinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: id=" + request.destinationId()));

        Attraction attraction = new Attraction();
        applyRequest(attraction, request, destination);
        return attractionRepository.save(attraction);
    }

    public Attraction update(Long id, AttractionRequest request) {
        validateFeeRange(request);
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attraction not found: id=" + id));
        Destination destination = destinationRepository.findById(request.destinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: id=" + request.destinationId()));

        applyRequest(attraction, request, destination);
        return attractionRepository.save(attraction);
    }

    public void setActive(Long id, boolean active) {
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attraction not found: id=" + id));
        attraction.setActive(active);
        attractionRepository.save(attraction);
    }

    private void validateFeeRange(AttractionRequest request) {
        if (request.entranceFeeMax().compareTo(request.entranceFeeMin()) < 0) {
            throw new IllegalArgumentException("entranceFeeMax must be greater than or equal to entranceFeeMin");
        }
    }

    private void applyRequest(Attraction attraction, AttractionRequest request, Destination destination) {
        attraction.setDestination(destination);
        attraction.setName(request.name());
        attraction.setCategory(request.category());
        attraction.setInterestTags(request.interestTags() != null ? request.interestTags() : new String[0]);
        attraction.setDescription(request.description());
        attraction.setEntranceFeeMin(request.entranceFeeMin());
        attraction.setEntranceFeeMax(request.entranceFeeMax());
        attraction.setAvgDurationHours(request.avgDurationHours());
        attraction.setCommunityBased(request.communityBased());
    }
}
