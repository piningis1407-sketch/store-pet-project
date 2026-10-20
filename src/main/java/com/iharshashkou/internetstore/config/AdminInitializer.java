package com.iharshashkou.internetstore.config;

import com.iharshashkou.internetstore.DTO.Role;
import com.iharshashkou.internetstore.entity.UserEntity;
import com.iharshashkou.internetstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createInitialAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.email:}") String email,
            @Value("${app.admin.password:}") String password
    ) {
        return args -> {
            // Если переменные не заданы, администратора не создаём.
            if (email.isBlank() || password.isBlank()) {
                return;
            }

            // Если пользователь уже существует, ничего не меняем.
            if (userRepository.existsByEmail(email)) {
                return;
            }

            UserEntity admin = UserEntity.builder()
                    .name("Administrator")
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(admin);
        };
    }
}
