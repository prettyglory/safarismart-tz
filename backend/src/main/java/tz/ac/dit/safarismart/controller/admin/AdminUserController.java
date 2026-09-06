package tz.ac.dit.safarismart.controller.admin;

import tz.ac.dit.safarismart.dto.admin.UserSummaryDto;
import tz.ac.dit.safarismart.service.admin.AdminUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final AdminUserService service;

    public AdminUserController(AdminUserService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserSummaryDto> getAll() {
        return service.findAll();
    }

    @PatchMapping("/{id}/active")
    public UserSummaryDto setActive(@PathVariable Long id, @RequestParam boolean active) {
        return service.setActive(id, active);
    }
}
