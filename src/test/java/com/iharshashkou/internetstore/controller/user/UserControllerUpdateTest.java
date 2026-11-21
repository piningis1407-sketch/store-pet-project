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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerUpdateTest extends BaseUserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnUpdatedUser_whenRequestIsValid() throws Exception {
        UserRequest request = new UserRequest(
                "Updated User",
                "updated@example.com",
                "newpassword123"
        );

        UserResponse response = new UserResponse(
                1L,
                "Updated User",
                "updated@example.com",
                Role.USER,
                Instant.parse("2026-01-01T10:00:00Z")
        );

        when(userService.update(1L, request)).thenReturn(response);

        mockMvc.perform(put("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnBadRequest_whenNameIsBlank() throws Exception {
        UserRequest request = new UserRequest(
                "",
                "updated@example.com",
                "newpassword123"
        );

        mockMvc.perform(put("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        UserRequest request = new UserRequest(
                "Updated User",
                "invalid-email",
                "newpassword123"
        );

        mockMvc.perform(put("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnBadRequest_whenPasswordIsTooShort() throws Exception {
        UserRequest request = new UserRequest(
                "Updated User",
                "updated@example.com",
                "1234567"
        );

        mockMvc.perform(put("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void update_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
        UserRequest request = new UserRequest(
                "Updated User",
                "updated@example.com",
                "newpassword123"
        );

        mockMvc.perform(put("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
