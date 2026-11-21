package com.iharshashkou.internetstore.controller.user;

import com.iharshashkou.internetstore.DTO.user.Role;
import com.iharshashkou.internetstore.DTO.user.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerGetTest extends BaseUserControllerTest {

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_shouldReturnUser_whenUserExists() throws Exception {
        UserResponse response = new UserResponse(
                1L,
                "Test User",
                "test@example.com",
                Role.USER,
                Instant.parse("2026-01-01T10:00:00Z")
        );

        when(userService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_shouldReturnUsers() throws Exception {
        UserResponse response = new UserResponse(
                1L,
                "Test User",
                "test@example.com",
                Role.USER,
                Instant.parse("2026-01-01T10:00:00Z")
        );

        when(userService.getAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test User"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"))
                .andExpect(jsonPath("$[0].role").value("USER"));
    }
}