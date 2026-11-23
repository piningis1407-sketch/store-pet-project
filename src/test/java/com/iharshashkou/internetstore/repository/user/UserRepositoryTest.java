package com.iharshashkou.internetstore.repository.user;

import com.iharshashkou.internetstore.DTO.user.Role;
import com.iharshashkou.internetstore.entity.User;
import com.iharshashkou.internetstore.repository.BaseProductRepositoryTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest extends BaseProductRepositoryTest {

    @Test
    void findByEmail_shouldReturnUserWhenEmailExists() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        userRepository.save(user);

        var result = userRepository.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(user.getEmail(), result.get().getEmail());
        assertEquals(user.getName(), result.get().getName());
    }

    @Test
    void existsByEmail_shouldReturnCorrectResult() {
        User user = User.builder()
                .name("Another User")
                .email("another@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        userRepository.save(user);

        assertTrue(userRepository.existsByEmail("another@example.com"));
        assertFalse(userRepository.existsByEmail("missing@example.com"));
    }
}
