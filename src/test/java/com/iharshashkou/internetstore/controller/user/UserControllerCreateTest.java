package com.iharshashkou.internetstore.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iharshashkou.internetstore.DTO.user.Role;
import com.iharshashkou.internetstore.DTO.user.UserRequest;
import com.iharshashkou.internetstore.DTO.user.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerCreateTest extends BaseUserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturnCreated_whenRequestIsValid() throws Exception {
        UserRequest request = new UserRequest(
                "Test User",
                "test@example.com",
                "password123"
        );

        UserResponse response = new UserResponse(
                1L,
                "Test User",
                "test@example.com",
                Role.USER,
                Instant.parse("2026-01-01T10:00:00Z")
        );

        when(userService.create(request)).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturnBadRequest_whenNameIsBlank() throws Exception {
        UserRequest request = new UserRequest(
                "",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        UserRequest request = new UserRequest(
                "Test User",
                "invalid-email",
                "password123"
        );

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturnBadRequest_whenPasswordIsTooShort() throws Exception {
        UserRequest request = new UserRequest(
                "Test User",
                "test@example.com",
                "1234567"
        );

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void create_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
        UserRequest request = new UserRequest(
                "Test User",
                "test@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}