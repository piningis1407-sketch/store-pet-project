package com.iharshashkou.internetstore.controller.customDesign;

import com.iharshashkou.internetstore.DTO.customDesign.CustomDesignResponse;
import com.iharshashkou.internetstore.entity.User;
import com.iharshashkou.internetstore.entity.customDesignEntity.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomDesignControllerImageTest
        extends BaseCustomDesignControllerTest {

    @Test
    void generateImage_withValidGuestToken_returnsOk() throws Exception {

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
                "/api/custom-designs/5/image",
                new BigDecimal("25000.00"),
                null,
                CustomDesignStatus.GENERATED,
                Instant.parse("2026-11-15T10:00:00Z")
        );

        when(customDesignService.generateImage(
                5L,
                null,
                "valid-guest-token"
        )).thenReturn(response);

        mockMvc.perform(post("/api/custom-designs/5/image")
                        .header("X-Guest-Token", "valid-guest-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.imageUrl")
                        .value("/api/custom-designs/5/image"))
                .andExpect(jsonPath("$.status").value("GENERATED"));

        verify(customDesignService).generateImage(
                5L,
                null,
                "valid-guest-token"
        );
    }

    @Test
    void getImage_withValidGuestToken_returnsPng() throws Exception {

        byte[] imageBytes = new byte[]{1, 2, 3, 4, 5};

        when(customDesignService.getImage(
                5L,
                null,
                "valid-guest-token"
        )).thenReturn(imageBytes);

        mockMvc.perform(get("/api/custom-designs/5/image")
                        .header("X-Guest-Token", "valid-guest-token"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(content().bytes(imageBytes));

        verify(customDesignService).getImage(
                5L,
                null,
                "valid-guest-token"
        );
    }

    @Test
    void generateImage_withAuthenticatedUser_returnsOk() throws Exception {

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(
                        User.builder()
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
                "/api/custom-designs/5/image",
                new BigDecimal("25000.00"),
                null,
                CustomDesignStatus.GENERATED,
                Instant.parse("2026-11-15T10:00:00Z")
        );

        when(customDesignService.generateImage(5L, 10L, null))
                .thenReturn(response);

        var authentication = new UsernamePasswordAuthenticationToken(
                "user@example.com",
                null,
                List.of()
        );

        mockMvc.perform(post("/api/custom-designs/5/image")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.imageUrl")
                        .value("/api/custom-designs/5/image"))
                .andExpect(jsonPath("$.status").value("GENERATED"));

        verify(customDesignService).generateImage(5L, 10L, null);
    }

    @Test
    void getImage_withAuthenticatedUser_returnsPng() throws Exception {

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(
                        User.builder()
                                .id(10L)
                                .email("user@example.com")
                                .build()
                ));

        byte[] imageBytes = new byte[]{1, 2, 3, 4, 5};

        when(customDesignService.getImage(5L, 10L, null))
                .thenReturn(imageBytes);

        var authentication = new UsernamePasswordAuthenticationToken(
                "user@example.com",
                null,
                List.of()
        );

        mockMvc.perform(get("/api/custom-designs/5/image")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(content().bytes(imageBytes));

        verify(customDesignService).getImage(5L, 10L, null);
    }
}
