package com.iharshashkou.internetstore.controller;

import com.iharshashkou.internetstore.DTO.customDesign.CustomDesignResponse;
import com.iharshashkou.internetstore.entity.customDesignEntity.*;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomDesignControllerSubmitTest
        extends BaseCustomDesignControllerTest {

    @Test
    void submitDesign_guest_returnsOk() throws Exception {

        CustomDesignResponse response = new CustomDesignResponse(
                5L,
                1L,
                "Столы",
                WoodType.OAK,
                DesignShape.RECTANGULAR,
                true,
                EpoxyColor.BLUE,
                DesignBackground.INTERIOR,
                120,
                80,
                75,
                null,
                "Тестовый проект",
                null,
                new BigDecimal("25000.00"),
                null,
                CustomDesignStatus.SUBMITTED,
                Instant.parse("2026-11-15T10:00:00Z")
        );

        when(customDesignService.submitDesign(
                eq(5L),
                isNull(),
                eq("valid-guest-token")
        )).thenReturn(response);

        mockMvc.perform(post("/api/custom-designs/5/submit")
                        .header("X-Guest-Token", "valid-guest-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value("SUBMITTED"));

        verify(customDesignService).submitDesign(
                5L,
                null,
                "valid-guest-token"
        );
    }

    @Test
    void submitDesign_authenticatedUser_returnsOk()
            throws Exception {

        CustomDesignResponse response = new CustomDesignResponse(
                5L,
                1L,
                "Столы",
                WoodType.OAK,
                DesignShape.RECTANGULAR,
                true,
                EpoxyColor.BLUE,
                DesignBackground.INTERIOR,
                120,
                80,
                75,
                null,
                "Тестовый проект",
                null,
                new BigDecimal("25000.00"),
                null,
                CustomDesignStatus.SUBMITTED,
                Instant.parse("2026-11-15T10:00:00Z")
        );

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(java.util.Optional.of(
                        com.iharshashkou.internetstore.entity.User.builder()
                                .id(10L)
                                .email("user@example.com")
                                .build()
                ));

        when(customDesignService.submitDesign(5L, 10L, null))
                .thenReturn(response);

        var authentication =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        "user@example.com",
                        null,
                        java.util.List.of()
                );

        mockMvc.perform(post("/api/custom-designs/5/submit")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value("SUBMITTED"));

        verify(customDesignService).submitDesign(5L, 10L, null);
    }
}
