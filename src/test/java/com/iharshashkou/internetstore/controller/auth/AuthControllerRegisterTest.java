package com.iharshashkou.internetstore.controller.auth;

import com.iharshashkou.internetstore.DTO.auth.AuthTokens;
import com.iharshashkou.internetstore.DTO.auth.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerRegisterTest extends BaseAuthControllerTest {

    @Test
    void register_shouldReturnCreated_whenRequestIsValid() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new AuthTokens(
                        "access-token",
                        "refresh-token",
                        "Bearer"
                ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Test User",
                                    "email": "test@example.com",
                                    "password": "Password123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().httpOnly("refreshToken", true));
    }

    @Test
    void register_shouldReturnBadRequest_whenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "",
                                    "email": "test@example.com",
                                    "password": "Password123"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Test User",
                                    "email": "invalid-email",
                                    "password": "Password123"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturnBadRequest_whenPasswordIsTooShort() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Test User",
                                    "email": "test@example.com",
                                    "password": "123"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
