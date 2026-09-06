package tz.ac.dit.safarismart.controller.admin;

import tz.ac.dit.safarismart.dto.admin.DestinationRequest;
import tz.ac.dit.safarismart.entity.Destination;
import tz.ac.dit.safarismart.service.admin.AdminDestinationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/destinations")
public class AdminDestinationController {

    private final AdminDestinationService service;

    public AdminDestinationController(AdminDestinationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Destination> create(@Valid @RequestBody DestinationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public Destination update(@PathVariable Long id, @Valid @RequestBody DestinationRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
