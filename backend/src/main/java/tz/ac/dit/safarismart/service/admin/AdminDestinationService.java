package tz.ac.dit.safarismart.service.admin;

import tz.ac.dit.safarismart.dto.admin.DestinationRequest;
import tz.ac.dit.safarismart.entity.Destination;
import tz.ac.dit.safarismart.entity.Region;
import tz.ac.dit.safarismart.exception.ResourceNotFoundException;
import tz.ac.dit.safarismart.repository.DestinationRepository;
import tz.ac.dit.safarismart.repository.RegionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminDestinationService {

    private final DestinationRepository destinationRepository;
    private final RegionRepository regionRepository;

    public AdminDestinationService(DestinationRepository destinationRepository,
                                    RegionRepository regionRepository) {
        this.destinationRepository = destinationRepository;
        this.regionRepository = regionRepository;
    }

    public Destination create(DestinationRequest request) {
        Region region = regionRepository.findById(request.regionId())
                .orElseThrow(() -> new ResourceNotFoundException("Region not found: id=" + request.regionId()));

        Destination destination = new Destination();
        applyRequest(destination, request, region);
        return destinationRepository.save(destination);
    }

    public Destination update(Long id, DestinationRequest request) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: id=" + id));
        Region region = regionRepository.findById(request.regionId())
                .orElseThrow(() -> new ResourceNotFoundException("Region not found: id=" + request.regionId()));

        applyRequest(destination, request, region);
        return destinationRepository.save(destination);
    }

    public void delete(Long id) {
        if (!destinationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Destination not found: id=" + id);
        }
        destinationRepository.deleteById(id); // ON DELETE RESTRICT protects destinations still referenced by attractions/trips
    }

    private void applyRequest(Destination destination, DestinationRequest request, Region region) {
        destination.setRegion(region);
        destination.setName(request.name());
        destination.setDescription(request.description());
        destination.setLatitude(request.latitude());
        destination.setLongitude(request.longitude());
    }
}
