package tz.ac.dit.safarismart.service.admin;

import tz.ac.dit.safarismart.dto.admin.AccommodationRequest;
import tz.ac.dit.safarismart.entity.Accommodation;
import tz.ac.dit.safarismart.entity.Destination;
import tz.ac.dit.safarismart.exception.ResourceNotFoundException;
import tz.ac.dit.safarismart.repository.AccommodationRepository;
import tz.ac.dit.safarismart.repository.DestinationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminAccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final DestinationRepository destinationRepository;

    public AdminAccommodationService(AccommodationRepository accommodationRepository,
                                      DestinationRepository destinationRepository) {
        this.accommodationRepository = accommodationRepository;
        this.destinationRepository = destinationRepository;
    }

    @Transactional(readOnly = true)
    public List<Accommodation> findAll() {
        return accommodationRepository.findAll();
    }

    public Accommodation create(AccommodationRequest request) {
        validatePriceRange(request.priceMin(), request.priceMax());
        Destination destination = destinationRepository.findById(request.destinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: id=" + request.destinationId()));

        Accommodation accommodation = new Accommodation();
        applyRequest(accommodation, request, destination);
        return accommodationRepository.save(accommodation);
    }

    public Accommodation update(Long id, AccommodationRequest request) {
        validatePriceRange(request.priceMin(), request.priceMax());
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found: id=" + id));
        Destination destination = destinationRepository.findById(request.destinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: id=" + request.destinationId()));

        applyRequest(accommodation, request, destination);
        return accommodationRepository.save(accommodation);
    }

    public void setActive(Long id, boolean active) {
        Accommodation accommodation = accommodationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found: id=" + id));
        accommodation.setActive(active);
        accommodationRepository.save(accommodation);
    }

    private void validatePriceRange(java.math.BigDecimal min, java.math.BigDecimal max) {
        if (max.compareTo(min) < 0) {
            throw new IllegalArgumentException("priceMax must be greater than or equal to priceMin");
        }
    }

    private void applyRequest(Accommodation accommodation, AccommodationRequest request, Destination destination) {
        accommodation.setDestination(destination);
        accommodation.setName(request.name());
        accommodation.setStyle(request.style());
        accommodation.setPriceMin(request.priceMin());
        accommodation.setPriceMax(request.priceMax());
        accommodation.setDescription(request.description());
    }
}
