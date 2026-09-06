package tz.ac.dit.safarismart.service.admin;

import tz.ac.dit.safarismart.dto.admin.GuidelineRequest;
import tz.ac.dit.safarismart.entity.Destination;
import tz.ac.dit.safarismart.entity.Region;
import tz.ac.dit.safarismart.entity.ResponsibleTourismGuideline;
import tz.ac.dit.safarismart.exception.ResourceNotFoundException;
import tz.ac.dit.safarismart.repository.DestinationRepository;
import tz.ac.dit.safarismart.repository.RegionRepository;
import tz.ac.dit.safarismart.repository.ResponsibleTourismGuidelineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminGuidelineService {

    private final ResponsibleTourismGuidelineRepository guidelineRepository;
    private final RegionRepository regionRepository;
    private final DestinationRepository destinationRepository;

    public AdminGuidelineService(ResponsibleTourismGuidelineRepository guidelineRepository,
                                  RegionRepository regionRepository,
                                  DestinationRepository destinationRepository) {
        this.guidelineRepository = guidelineRepository;
        this.regionRepository = regionRepository;
        this.destinationRepository = destinationRepository;
    }

    @Transactional(readOnly = true)
    public List<ResponsibleTourismGuideline> findAll() {
        return guidelineRepository.findAll();
    }

    public ResponsibleTourismGuideline create(GuidelineRequest request) {
        ResponsibleTourismGuideline guideline = new ResponsibleTourismGuideline();
        applyRequest(guideline, request);
        return guidelineRepository.save(guideline);
    }

    public ResponsibleTourismGuideline update(Long id, GuidelineRequest request) {
        ResponsibleTourismGuideline guideline = guidelineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guideline not found: id=" + id));
        applyRequest(guideline, request);
        return guidelineRepository.save(guideline);
    }

    public void delete(Long id) {
        if (!guidelineRepository.existsById(id)) {
            throw new ResourceNotFoundException("Guideline not found: id=" + id);
        }
        guidelineRepository.deleteById(id);
    }

    private void applyRequest(ResponsibleTourismGuideline guideline, GuidelineRequest request) {
        Region region = null;
        Destination destination = null;

        if (request.regionId() != null) {
            region = regionRepository.findById(request.regionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Region not found: id=" + request.regionId()));
        }
        if (request.destinationId() != null) {
            destination = destinationRepository.findById(request.destinationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Destination not found: id=" + request.destinationId()));
        }

        guideline.setRegion(region);
        guideline.setDestination(destination);
        guideline.setCategory(request.category());
        guideline.setGuidelineText(request.guidelineText());
    }
}
