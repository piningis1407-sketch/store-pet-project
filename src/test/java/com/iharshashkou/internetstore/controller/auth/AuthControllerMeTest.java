package com.iharshashkou.internetstore.controller.auth;

import com.iharshashkou.internetstore.DTO.user.Role;
import com.iharshashkou.internetstore.DTO.user.UserProfileResponse;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerMeTest extends BaseAuthControllerTest {

    @Test
    void getCurrentUser_shouldReturnProfile_whenAuthenticated() throws Exception {
        when(authService.getCurrentUserProfile("test@example.com"))
                .thenReturn(new UserProfileResponse(
                        "Test User",
                        "test@example.com",
                        Role.USER
                ));

        mockMvc.perform(get("/api/auth/me")
                        .principal(() -> "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
}
