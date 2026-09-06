package tz.ac.dit.safarismart.controller.admin;

import tz.ac.dit.safarismart.dto.admin.GuidelineRequest;
import tz.ac.dit.safarismart.entity.ResponsibleTourismGuideline;
import tz.ac.dit.safarismart.service.admin.AdminGuidelineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/guidelines")
public class AdminGuidelineController {

    private final AdminGuidelineService service;

    public AdminGuidelineController(AdminGuidelineService service) {
        this.service = service;
    }

    @GetMapping
    public java.util.List<ResponsibleTourismGuideline> getAll() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<ResponsibleTourismGuideline> create(@Valid @RequestBody GuidelineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponsibleTourismGuideline update(@PathVariable Long id, @Valid @RequestBody GuidelineRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
