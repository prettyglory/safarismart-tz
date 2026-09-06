package tz.ac.dit.safarismart.controller.admin;

import tz.ac.dit.safarismart.dto.admin.RegionRequest;
import tz.ac.dit.safarismart.entity.Region;
import tz.ac.dit.safarismart.service.admin.AdminRegionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/regions")
public class AdminRegionController {

    private final AdminRegionService service;

    public AdminRegionController(AdminRegionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Region> getAll() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<Region> create(@Valid @RequestBody RegionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public Region update(@PathVariable Long id, @Valid @RequestBody RegionRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
