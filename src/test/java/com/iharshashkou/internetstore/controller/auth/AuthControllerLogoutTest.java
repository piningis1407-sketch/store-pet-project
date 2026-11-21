package com.iharshashkou.internetstore.controller.auth;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerLogoutTest extends BaseAuthControllerTest {

    @Test
    void logout_shouldReturnNoContent_whenRefreshTokenIsValid() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .cookie(new jakarta.servlet.http.Cookie(
                                "refreshToken",
                                "valid-refresh-token"
                        )))
                .andExpect(status().isNoContent())
                .andExpect(cookie().exists("refreshToken"));

        verify(authService).logout("valid-refresh-token");
    }

    @Test
    void logout_shouldReturnNoContent_whenRefreshTokenIsMissing() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(cookie().exists("refreshToken"));

        verify(authService, never()).logout(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void logout_shouldReturnNoContent_whenRefreshTokenIsBlank() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .cookie(new jakarta.servlet.http.Cookie(
                                "refreshToken",
                                ""
                        )))
                .andExpect(status().isNoContent())
                .andExpect(cookie().exists("refreshToken"));

        verify(authService, never()).logout(org.mockito.ArgumentMatchers.anyString());
    }
}
