package com.iharshashkou.internetstore.service.user;

import com.iharshashkou.internetstore.DTO.user.UserRequest;
import com.iharshashkou.internetstore.DTO.user.UserResponse;
import com.iharshashkou.internetstore.entity.User;
import com.iharshashkou.internetstore.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceUpdateTest extends BaseUserServiceTest {

    @Test
    void update_validRequest_returnsUpdatedUser() {
        Long userId = 1L;

        UserRequest request = new UserRequest(
                "Updated User",
                "updated@example.com",
                "newPassword123"
        );

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old User");
        existingUser.setEmail("old@example.com");

        UserResponse response = mock(UserResponse.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password()))
                .thenReturn("encoded-new-password");
        when(userRepository.save(existingUser))
                .thenReturn(existingUser);
        when(userMapper.toResponse(existingUser))
                .thenReturn(response);

        UserResponse result = userService.update(userId, request);

        assertNotNull(result);
        assertSame(response, result);

        assertEquals("Updated User", existingUser.getName());
        assertEquals("updated@example.com", existingUser.getEmail());
        assertEquals("encoded-new-password", existingUser.getPassword());

        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(request.email());
        verify(userRepository).save(existingUser);
        verify(passwordEncoder).encode(request.password());
    }

    @Test
    void update_emailAlreadyUsedByAnotherUser_throwsException() {
        Long userId = 1L;

        UserRequest request = new UserRequest(
                "Updated User",
                "taken@example.com",
                "newPassword123"
        );

        User existingUser = new User();
        existingUser.setId(userId);

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("taken@example.com");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(anotherUser));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.update(userId, request)
        );

        assertEquals("Этот email уже используется", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder, userMapper);
    }

    @Test
    void update_sameEmail_returnsUpdatedUser() {
        Long userId = 1L;

        UserRequest request = new UserRequest(
                "Updated User",
                "user@example.com",
                "newPassword123"
        );

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old User");
        existingUser.setEmail("user@example.com");

        UserResponse response = mock(UserResponse.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(request.password()))
                .thenReturn("encoded-password");
        when(userRepository.save(existingUser))
                .thenReturn(existingUser);
        when(userMapper.toResponse(existingUser))
                .thenReturn(response);

        UserResponse result = userService.update(userId, request);

        assertSame(response, result);
        assertEquals("Updated User", existingUser.getName());
        assertEquals("user@example.com", existingUser.getEmail());

        verify(userRepository).save(existingUser);
    }

    @Test
    void update_userNotFound_throwsException() {
        Long userId = 999L;

        UserRequest request = new UserRequest(
                "Updated User",
                "updated@example.com",
                "newPassword123"
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.update(userId, request)
        );

        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder, userMapper);
    }
}