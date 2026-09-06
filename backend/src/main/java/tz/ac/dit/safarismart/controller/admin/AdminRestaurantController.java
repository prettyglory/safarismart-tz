package tz.ac.dit.safarismart.controller.admin;

import tz.ac.dit.safarismart.dto.admin.RestaurantRequest;
import tz.ac.dit.safarismart.entity.Restaurant;
import tz.ac.dit.safarismart.service.admin.AdminRestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/restaurants")
public class AdminRestaurantController {

    private final AdminRestaurantService service;

    public AdminRestaurantController(AdminRestaurantService service) {
        this.service = service;
    }

    @GetMapping
    public java.util.List<Restaurant> getAll() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<Restaurant> create(@Valid @RequestBody RestaurantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public Restaurant update(@PathVariable Long id, @Valid @RequestBody RestaurantRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> setActive(@PathVariable Long id, @RequestParam boolean active) {
        service.setActive(id, active);
        return ResponseEntity.noContent().build();
    }
}
