package tz.ac.dit.safarismart.service;

import tz.ac.dit.safarismart.dto.AuthResponse;
import tz.ac.dit.safarismart.dto.LoginRequest;
import tz.ac.dit.safarismart.dto.RegisterRequest;
import tz.ac.dit.safarismart.entity.AppUser;
import tz.ac.dit.safarismart.entity.UserRole;
import tz.ac.dit.safarismart.exception.EmailAlreadyExistsException;
import tz.ac.dit.safarismart.repository.AppUserRepository;
import tz.ac.dit.safarismart.security.CustomUserDetails;
import tz.ac.dit.safarismart.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(AppUserRepository appUserRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        AuthenticationManager authenticationManager) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (appUserRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        AppUser user = new AppUser();
        user.setFullName(request.fullName());
        user.setEmail(request.email().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.TOURIST); // registration always creates a TOURIST account;
                                         // ADMIN accounts are provisioned separately (Phase 6), never self-registered

        AppUser saved = appUserRepository.save(user);
        CustomUserDetails userDetails = new CustomUserDetails(saved);
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.of(token, saved.getId(), saved.getFullName(), saved.getEmail(), saved.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().toLowerCase(), request.password())
        );

        AppUser user = appUserRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found -- this should not happen"));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.of(token, user.getId(), user.getFullName(), user.getEmail(), user.getRole().name());
    }
}
