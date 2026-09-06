package tz.ac.dit.safarismart.service.admin;

import tz.ac.dit.safarismart.dto.admin.TransportOptionRequest;
import tz.ac.dit.safarismart.entity.Destination;
import tz.ac.dit.safarismart.entity.TransportOption;
import tz.ac.dit.safarismart.exception.ResourceNotFoundException;
import tz.ac.dit.safarismart.repository.DestinationRepository;
import tz.ac.dit.safarismart.repository.TransportOptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminTransportOptionService {

    private final TransportOptionRepository transportOptionRepository;
    private final DestinationRepository destinationRepository;

    public AdminTransportOptionService(TransportOptionRepository transportOptionRepository,
                                        DestinationRepository destinationRepository) {
        this.transportOptionRepository = transportOptionRepository;
        this.destinationRepository = destinationRepository;
    }

    @Transactional(readOnly = true)
    public List<TransportOption> findAll() {
        return transportOptionRepository.findAll();
    }

    public TransportOption create(TransportOptionRequest request) {
        validatePriceRange(request.priceMin(), request.priceMax());
        Destination destination = destinationRepository.findById(request.destinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: id=" + request.destinationId()));

        TransportOption option = new TransportOption();
        applyRequest(option, request, destination);
        return transportOptionRepository.save(option);
    }

    public TransportOption update(Long id, TransportOptionRequest request) {
        validatePriceRange(request.priceMin(), request.priceMax());
        TransportOption option = transportOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transport option not found: id=" + id));
        Destination destination = destinationRepository.findById(request.destinationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: id=" + request.destinationId()));

        applyRequest(option, request, destination);
        return transportOptionRepository.save(option);
    }

    public void setActive(Long id, boolean active) {
        TransportOption option = transportOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transport option not found: id=" + id));
        option.setActive(active);
        transportOptionRepository.save(option);
    }

    private void validatePriceRange(java.math.BigDecimal min, java.math.BigDecimal max) {
        if (max.compareTo(min) < 0) {
            throw new IllegalArgumentException("priceMax must be greater than or equal to priceMin");
        }
    }

    private void applyRequest(TransportOption option, TransportOptionRequest request, Destination destination) {
        option.setDestination(destination);
        option.setType(request.type());
        option.setDescription(request.description());
        option.setPriceMin(request.priceMin());
        option.setPriceMax(request.priceMax());
    }
}
