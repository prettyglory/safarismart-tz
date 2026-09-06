package tz.ac.dit.safarismart.dto.admin;

public record UserSummaryDto(
        Long id,
        String fullName,
        String email,
        String role,
        boolean active
) {
}
