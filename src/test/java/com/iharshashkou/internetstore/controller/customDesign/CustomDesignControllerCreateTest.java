package com.iharshashkou.internetstore.controller.customDesign;

import com.iharshashkou.internetstore.DTO.user.Role;
import com.iharshashkou.internetstore.DTO.customDesign.*;
import com.iharshashkou.internetstore.entity.User;
import com.iharshashkou.internetstore.entity.customDesignEntity.*;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomDesignControllerCreateTest
        extends BaseCustomDesignControllerTest {

    @Test
    void createDesign_guest_returnsCreatedResponseWithGuestToken()
            throws Exception {

        CustomDesignResponse design = new CustomDesignResponse(
                1L,
                1L,
                "Столы",
                WoodType.values()[0],
                DesignShape.values()[0],
                false,
                null,
                DesignBackground.values()[0],
                100,
                60,
                5,
                null,
                "Тестовый проект",
                null,
                new BigDecimal("12500.00"),
                null,
                CustomDesignStatus.GENERATED,
                Instant.parse("2026-11-15T10:00:00Z")
        );

        CreateCustomDesignResponse response =
                new CreateCustomDesignResponse(design, "guest-token");

        when(customDesignService.createDesign(
                isNull(),
                any(CreateCustomDesignRequest.class)
        )).thenReturn(response);

        String requestJson = """
                {
                    "categoryId": 1,
                    "woodType": "%s",
                    "shape": "%s",
                    "epoxyEnabled": false,
                    "background": "%s",
                    "lengthCm": 100,
                    "widthCm": 60,
                    "heightCm": 5,
                    "comment": "Тестовый проект"
                }
                """.formatted(
                WoodType.values()[0].name(),
                DesignShape.values()[0].name(),
                DesignBackground.values()[0].name()
        );

        mockMvc.perform(post("/api/custom-designs")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.design.id").value(1))
                .andExpect(jsonPath("$.design.status").value("GENERATED"))
                .andExpect(jsonPath("$.guestAccessToken").value("guest-token"));
    }

    @Test
    void createDesign_authenticatedUser_returnsCreatedResponse()
            throws Exception {

        User user = User.builder()
                .id(10L)
                .email("user@example.com")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        CustomDesignResponse design = new CustomDesignResponse(
                2L,
                1L,
                "Столы",
                WoodType.values()[0],
                DesignShape.values()[0],
                false,
                null,
                DesignBackground.values()[0],
                100,
                60,
                5,
                null,
                "Проект пользователя",
                null,
                new BigDecimal("12500.00"),
                null,
                CustomDesignStatus.GENERATED,
                Instant.parse("2026-11-15T10:00:00Z")
        );

        CreateCustomDesignResponse response =
                new CreateCustomDesignResponse(design, null);

        when(customDesignService.createDesign(
                eq(10L),
                any(CreateCustomDesignRequest.class)
        )).thenReturn(response);

        var authentication = new UsernamePasswordAuthenticationToken(
                "user@example.com",
                null,
                List.of()
        );

        String requestJson = """
            {
                "categoryId": 1,
                "woodType": "%s",
                "shape": "%s",
                "epoxyEnabled": false,
                "background": "%s",
                "lengthCm": 100,
                "widthCm": 60,
                "heightCm": 5,
                "comment": "Проект пользователя"
            }
            """.formatted(
                WoodType.values()[0].name(),
                DesignShape.values()[0].name(),
                DesignBackground.values()[0].name()
        );

        mockMvc.perform(post("/api/custom-designs")
                        .principal(authentication)
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.design.id").value(2))
                .andExpect(jsonPath("$.guestAccessToken").doesNotExist());

        verify(customDesignService).createDesign(
                eq(10L),
                any(CreateCustomDesignRequest.class)
        );
    }
}