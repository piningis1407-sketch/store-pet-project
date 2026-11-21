package com.iharshashkou.internetstore.controller.auth;

import com.iharshashkou.internetstore.DTO.auth.AuthTokens;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerRefreshTest extends BaseAuthControllerTest {

    @Test
    void refresh_shouldReturnOk_whenRefreshTokenIsValid() throws Exception {
        when(authService.refresh(anyString()))
                .thenReturn(new AuthTokens(
                        "new-access-token",
                        "new-refresh-token",
                        "Bearer"
                ));

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new jakarta.servlet.http.Cookie(
                                "refreshToken",
                                "old-refresh-token"
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().httpOnly("refreshToken", true));
    }

    @Test
    void refresh_shouldReturnUnauthorized_whenRefreshTokenIsMissing() throws Exception {
        mockMvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isUnauthorized())
                .andExpect(cookie().exists("refreshToken"));
    }

    @Test
    void refresh_shouldReturnUnauthorized_whenRefreshTokenIsBlank() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new jakarta.servlet.http.Cookie(
                                "refreshToken",
                                ""
                        )))
                .andExpect(status().isUnauthorized())
                .andExpect(cookie().exists("refreshToken"));
    }
}
