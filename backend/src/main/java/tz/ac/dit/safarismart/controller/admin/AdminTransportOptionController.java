package tz.ac.dit.safarismart.controller.admin;

import tz.ac.dit.safarismart.dto.admin.TransportOptionRequest;
import tz.ac.dit.safarismart.entity.TransportOption;
import tz.ac.dit.safarismart.service.admin.AdminTransportOptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/transport-options")
public class AdminTransportOptionController {

    private final AdminTransportOptionService service;

    public AdminTransportOptionController(AdminTransportOptionService service) {
        this.service = service;
    }

    @GetMapping
    public java.util.List<TransportOption> getAll() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<TransportOption> create(@Valid @RequestBody TransportOptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public TransportOption update(@PathVariable Long id, @Valid @RequestBody TransportOptionRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> setActive(@PathVariable Long id, @RequestParam boolean active) {
        service.setActive(id, active);
        return ResponseEntity.noContent().build();
    }
}
