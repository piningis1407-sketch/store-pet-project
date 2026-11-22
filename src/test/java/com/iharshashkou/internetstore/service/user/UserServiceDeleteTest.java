package com.iharshashkou.internetstore.service.user;

import com.iharshashkou.internetstore.entity.User;
import com.iharshashkou.internetstore.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceDeleteTest extends BaseUserServiceTest {

    @Test
    void delete_existingUser_deletesUser() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.delete(userId));

        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }

    @Test
    void delete_userNotFound_throwsException() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.delete(userId)
        );

        verify(userRepository).findById(userId);
        verify(userRepository, never()).delete(any(User.class));
    }
}
