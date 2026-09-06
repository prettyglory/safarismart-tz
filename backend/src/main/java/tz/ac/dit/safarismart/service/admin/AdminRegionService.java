package tz.ac.dit.safarismart.service.admin;

import tz.ac.dit.safarismart.dto.admin.RegionRequest;
import tz.ac.dit.safarismart.entity.Region;
import tz.ac.dit.safarismart.exception.ResourceNotFoundException;
import tz.ac.dit.safarismart.repository.RegionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminRegionService {

    private final RegionRepository regionRepository;

    public AdminRegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Transactional(readOnly = true)
    public List<Region> findAll() {
        return regionRepository.findAll();
    }

    public Region create(RegionRequest request) {
        Region region = new Region();
        region.setName(request.name());
        return regionRepository.save(region);
    }

    public Region update(Long id, RegionRequest request) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region not found: id=" + id));
        region.setName(request.name());
        return regionRepository.save(region);
    }

    public void delete(Long id) {
        if (!regionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Region not found: id=" + id);
        }
        regionRepository.deleteById(id); // ON DELETE RESTRICT in DB protects regions with destinations
    }
}
