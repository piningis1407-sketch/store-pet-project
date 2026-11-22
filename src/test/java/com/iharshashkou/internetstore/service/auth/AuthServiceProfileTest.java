package com.iharshashkou.internetstore.service.auth;

import com.iharshashkou.internetstore.entity.User;
import com.iharshashkou.internetstore.DTO.user.Role;
import com.iharshashkou.internetstore.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceProfileTest extends BaseAuthServiceTest {

    @Test
    void getCurrentUserProfile_existingUser_returnsProfile() {
        String email = "user@example.com";

        User user = new User();
        user.setName("Test User");
        user.setEmail(email);
        user.setRole(Role.USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        var result = authService.getCurrentUserProfile(email);

        assertNotNull(result);
        assertEquals("Test User", result.name());
        assertEquals(email, result.email());
        assertEquals(Role.USER, result.role());

        verify(userRepository).findByEmail(email);
    }

    @Test
    void getCurrentUserProfile_userNotFound_throwsException() {
        String email = "unknown@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> authService.getCurrentUserProfile(email)
        );

        verify(userRepository).findByEmail(email);
    }
}
