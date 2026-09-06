package tz.ac.dit.safarismart.controller.admin;

import tz.ac.dit.safarismart.dto.admin.AccommodationRequest;
import tz.ac.dit.safarismart.entity.Accommodation;
import tz.ac.dit.safarismart.service.admin.AdminAccommodationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/accommodations")
public class AdminAccommodationController {

    private final AdminAccommodationService service;

    public AdminAccommodationController(AdminAccommodationService service) {
        this.service = service;
    }

    @GetMapping
    public java.util.List<Accommodation> getAll() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<Accommodation> create(@Valid @RequestBody AccommodationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public Accommodation update(@PathVariable Long id, @Valid @RequestBody AccommodationRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> setActive(@PathVariable Long id, @RequestParam boolean active) {
        service.setActive(id, active);
        return ResponseEntity.noContent().build();
    }
}
