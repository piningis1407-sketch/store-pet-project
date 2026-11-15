package com.iharshashkou.internetstore.controller;

import com.iharshashkou.internetstore.DTO.customDesign.CustomDesignAdminResponse;
import com.iharshashkou.internetstore.DTO.customDesign.ReviewCustomDesignRequest;
import com.iharshashkou.internetstore.entity.customDesignEntity.CustomDesignStatus;
import com.iharshashkou.internetstore.entity.customDesignEntity.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomDesignControllerAdminTest
        extends BaseCustomDesignControllerTest {

    @Test
    void getAllDesigns_returnsOk() throws Exception {

        when(customDesignService.getAllDesigns())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/custom-designs/admin"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(customDesignService).getAllDesigns();
    }
    @Test
    void reviewDesign_approved_returnsOk() throws Exception {

        CustomDesignAdminResponse response =
                new CustomDesignAdminResponse(
                        5L,
                        10L,
                        "Тестовый пользователь",
                        "user@example.com",
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
                        "Проект одобрен",
                        null,
                        new BigDecimal("25000.00"),
                        new BigDecimal("30000.00"),
                        CustomDesignStatus.APPROVED,
                        Instant.parse("2026-11-15T10:00:00Z")
                );

        when(customDesignService.reviewDesign(
                eq(5L),
                any(ReviewCustomDesignRequest.class)
        )).thenReturn(response);

        mockMvc.perform(patch("/api/custom-designs/admin/5/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "status": "APPROVED",
                                "finalPrice": 30000.00,
                                "adminComment": "Проект одобрен"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.userName").value("Тестовый пользователь"))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.finalPrice").value(30000.00))
                .andExpect(jsonPath("$.adminComment").value("Проект одобрен"));

        verify(customDesignService).reviewDesign(
                eq(5L),
                any(ReviewCustomDesignRequest.class)
        );
    }

    @Test
    void reviewDesign_rejected_returnsOk() throws Exception {

        CustomDesignAdminResponse response =
                new CustomDesignAdminResponse(
                        5L,
                        10L,
                        "Тестовый пользователь",
                        "user@example.com",
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
                        "Невозможно выполнить проект",
                        null,
                        new BigDecimal("25000.00"),
                        null,
                        CustomDesignStatus.REJECTED,
                        Instant.parse("2026-11-15T10:00:00Z")
                );

        when(customDesignService.reviewDesign(
                eq(5L),
                any(ReviewCustomDesignRequest.class)
        )).thenReturn(response);

        mockMvc.perform(patch("/api/custom-designs/admin/5/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "status": "REJECTED",
                                "adminComment": "Невозможно выполнить проект"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.finalPrice").doesNotExist())
                .andExpect(jsonPath("$.adminComment")
                        .value("Невозможно выполнить проект"));

        verify(customDesignService).reviewDesign(
                eq(5L),
                any(ReviewCustomDesignRequest.class)
        );
    }

    @Test
    void reviewDesign_adminCommentTooLong_returnsBadRequest()
            throws Exception {

        String longComment = "А".repeat(2001);

        mockMvc.perform(patch("/api/custom-designs/admin/5/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "status": "REJECTED",
                                "finalPrice": null,
                                "adminComment": "%s"
                            }
                            """.formatted(longComment)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customDesignService);
    }

    @Test
    void reviewDesign_withoutStatus_returnsBadRequest()
            throws Exception {

        mockMvc.perform(patch("/api/custom-designs/admin/5/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "finalPrice": 30000.00,
                                "adminComment": "Проверка"
                            }
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customDesignService);
    }
}
