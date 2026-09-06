-- Seeds one ADMIN account for initial system access.
-- Password: ChangeMe123!  (CHANGE THIS after first login in any real deployment)
-- Hash generated with BCrypt, strength 10 -- compatible with Spring Security's BCryptPasswordEncoder.
INSERT INTO app_user (full_name, email, password_hash, role, is_active)
VALUES (
    'System Administrator',
    'admin@safarismart.tz',
    '$2b$10$sIaPZzblrb0jeTNTnOgPI.S0IEGNI1WpuIR6iThpbuWXz5jLMqrHG',
    'ADMIN',
    TRUE
);
