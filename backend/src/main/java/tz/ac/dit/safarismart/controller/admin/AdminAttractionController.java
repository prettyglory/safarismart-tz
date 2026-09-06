package tz.ac.dit.safarismart.controller.admin;

import tz.ac.dit.safarismart.dto.admin.AttractionRequest;
import tz.ac.dit.safarismart.entity.Attraction;
import tz.ac.dit.safarismart.service.admin.AdminAttractionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/attractions")
public class AdminAttractionController {

    private final AdminAttractionService service;

    public AdminAttractionController(AdminAttractionService service) {
        this.service = service;
    }

    @GetMapping
    public java.util.List<Attraction> getAll() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<Attraction> create(@Valid @RequestBody AttractionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public Attraction update(@PathVariable Long id, @Valid @RequestBody AttractionRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> setActive(@PathVariable Long id, @RequestParam boolean active) {
        service.setActive(id, active);
        return ResponseEntity.noContent().build();
    }
}
