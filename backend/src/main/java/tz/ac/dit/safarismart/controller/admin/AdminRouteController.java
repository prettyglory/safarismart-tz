package tz.ac.dit.safarismart.controller.admin;

import tz.ac.dit.safarismart.dto.admin.InterDestinationRouteRequest;
import tz.ac.dit.safarismart.entity.InterDestinationRoute;
import tz.ac.dit.safarismart.service.admin.AdminInterDestinationRouteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/routes")
public class AdminRouteController {

    private final AdminInterDestinationRouteService service;

    public AdminRouteController(AdminInterDestinationRouteService service) {
        this.service = service;
    }

    @GetMapping
    public java.util.List<InterDestinationRoute> getAll() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<InterDestinationRoute> create(@Valid @RequestBody InterDestinationRouteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public InterDestinationRoute update(@PathVariable Long id, @Valid @RequestBody InterDestinationRouteRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> setActive(@PathVariable Long id, @RequestParam boolean active) {
        service.setActive(id, active);
        return ResponseEntity.noContent().build();
    }
}
