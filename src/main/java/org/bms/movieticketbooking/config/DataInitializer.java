package org.bms.movieticketbooking.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bms.movieticketbooking.entity.supporting.User;
import org.bms.movieticketbooking.enums.Role;
import org.bms.movieticketbooking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@moviebooking.com")) {
            User admin = User.builder()
                    .email("admin@moviebooking.com")
                    .password(passwordEncoder.encode("admin123"))
                    .name("Admin")
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Admin user created: admin@moviebooking.com / admin123");
        }
    }
}
