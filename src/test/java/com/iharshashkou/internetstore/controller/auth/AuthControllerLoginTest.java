package com.iharshashkou.internetstore.controller.auth;

import com.iharshashkou.internetstore.DTO.auth.AuthTokens;
import com.iharshashkou.internetstore.DTO.auth.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerLoginTest extends BaseAuthControllerTest {

    @Test
    void login_shouldReturnOk_whenCredentialsAreValid() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new AuthTokens(
                        "access-token",
                        "refresh-token",
                        "Bearer"
                ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "test@example.com",
                                    "password": "Password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().httpOnly("refreshToken", true));
    }

    @Test
    void login_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "invalid-email",
                                    "password": "Password123"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturnBadRequest_whenPasswordIsBlank() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "test@example.com",
                                    "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
