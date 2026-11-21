package com.iharshashkou.internetstore.controller.customDesign;

import com.iharshashkou.internetstore.DTO.customDesign.CustomDesignResponse;

import com.iharshashkou.internetstore.entity.customDesignEntity.*;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

class CustomDesignControllerUserAccessTest
        extends BaseCustomDesignControllerTest {

    @Test
    void getUserDesigns_authenticatedUser_returnsOk()
            throws Exception {

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(java.util.Optional.of(
                        com.iharshashkou.internetstore.entity.User.builder()
                                .id(10L)
                                .email("user@example.com")
                                .build()
                ));

        when(customDesignService.getUserDesigns(10L))
                .thenReturn(List.of());

        var authentication =
                new UsernamePasswordAuthenticationToken(
                        "user@example.com",
                        null,
                        List.of()
                );

        mockMvc.perform(get("/api/custom-designs")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(customDesignService).getUserDesigns(eq(10L));
    }

    @Test
    void getUserDesign_authenticatedUser_returnsOk()
            throws Exception {

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(java.util.Optional.of(
                        com.iharshashkou.internetstore.entity.User.builder()
                                .id(10L)
                                .email("user@example.com")
                                .build()
                ));

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
                CustomDesignStatus.GENERATED,
                Instant.parse("2026-11-15T10:00:00Z")
        );

        when(customDesignService.getUserDesign(10L, 5L))
                .thenReturn(response);

        var authentication =
                new UsernamePasswordAuthenticationToken(
                        "user@example.com",
                        null,
                        List.of()
                );

        mockMvc.perform(get("/api/custom-designs/5")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.categoryName").value("Столы"))
                .andExpect(jsonPath("$.status").value("GENERATED"));

        verify(customDesignService).getUserDesign(10L, 5L);
    }
}
