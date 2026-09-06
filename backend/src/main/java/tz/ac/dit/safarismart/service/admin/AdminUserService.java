package tz.ac.dit.safarismart.service.admin;

import tz.ac.dit.safarismart.dto.admin.UserSummaryDto;
import tz.ac.dit.safarismart.entity.AppUser;
import tz.ac.dit.safarismart.exception.ResourceNotFoundException;
import tz.ac.dit.safarismart.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminUserService {

    private final AppUserRepository appUserRepository;

    public AdminUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Transactional(readOnly = true)
    public List<UserSummaryDto> findAll() {
        return appUserRepository.findAll().stream().map(this::toSummary).toList();
    }

    public UserSummaryDto setActive(Long id, boolean active) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: id=" + id));
        user.setActive(active);
        return toSummary(appUserRepository.save(user));
    }

    private UserSummaryDto toSummary(AppUser user) {
        return new UserSummaryDto(user.getId(), user.getFullName(), user.getEmail(),
                user.getRole().name(), user.isActive());
    }
}
