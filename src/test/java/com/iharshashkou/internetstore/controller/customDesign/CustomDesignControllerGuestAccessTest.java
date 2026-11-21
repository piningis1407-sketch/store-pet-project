package com.iharshashkou.internetstore.controller.customDesign;

import com.iharshashkou.internetstore.DTO.customDesign.CustomDesignResponse;
import com.iharshashkou.internetstore.entity.customDesignEntity.*;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomDesignControllerGuestAccessTest
        extends BaseCustomDesignControllerTest {

    @Test
    void getGuestDesign_withValidToken_returnsOk()
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
                CustomDesignStatus.GENERATED,
                Instant.parse("2026-11-15T10:00:00Z")
        );

        when(customDesignService.getGuestDesign(
                5L,
                "valid-guest-token"
        )).thenReturn(response);

        mockMvc.perform(get("/api/custom-designs/guest/5")
                        .header("X-Guest-Token", "valid-guest-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryName").value("Столы"))
                .andExpect(jsonPath("$.woodType").value("OAK"))
                .andExpect(jsonPath("$.status").value("GENERATED"));
    }

    @Test
    void getGuestDesign_withoutToken_returnsBadRequest()
            throws Exception {

        mockMvc.perform(get("/api/custom-designs/guest/5"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customDesignService);
    }
}
