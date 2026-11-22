package com.iharshashkou.internetstore.service.user;

import com.iharshashkou.internetstore.DTO.user.UserRequest;
import com.iharshashkou.internetstore.DTO.user.UserResponse;
import com.iharshashkou.internetstore.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceCreateTest extends BaseUserServiceTest {

    @Test
    void create_validRequest_returnsCreatedUser() {
        UserRequest request = new UserRequest(
                "Test User",
                "test@example.com",
                "password123"
        );

        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Test User");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encoded-password");

        UserResponse response = mock(UserResponse.class);

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(response);

        UserResponse result = userService.create(request);

        assertNotNull(result);
        assertSame(response, result);
        assertEquals("encoded-password", user.getPassword());

        verify(userRepository).existsByEmail(request.email());
        verify(userRepository).save(user);
        verify(userMapper).toResponse(savedUser);
    }

    @Test
    void create_existingEmail_throwsException() {
        UserRequest request = new UserRequest(
                "Test User",
                "test@example.com",
                "password123"
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.create(request)
        );

        assertEquals(
                "Пользователь с таким email уже существует",
                exception.getMessage()
        );

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(userMapper, passwordEncoder);
    }
}
