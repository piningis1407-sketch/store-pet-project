package com.iharshashkou.internetstore.service.user;

import com.iharshashkou.internetstore.DTO.user.UserResponse;
import com.iharshashkou.internetstore.entity.User;
import com.iharshashkou.internetstore.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceGetByIdTest extends BaseUserServiceTest {

    @Test
    void getById_existingUser_returnsUserResponse() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@example.com");

        UserResponse response = mock(UserResponse.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.getById(userId);

        assertNotNull(result);
        assertSame(response, result);

        verify(userRepository).findById(userId);
        verify(userMapper).toResponse(user);
    }

    @Test
    void getById_userNotFound_throwsException() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getById(userId)
        );

        verify(userRepository).findById(userId);
        verifyNoInteractions(userMapper);
    }
}
