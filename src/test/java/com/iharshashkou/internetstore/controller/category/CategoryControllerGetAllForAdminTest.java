package com.iharshashkou.internetstore.controller.category;

import com.iharshashkou.internetstore.DTO.category.CategoryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryControllerGetAllForAdminTest extends BaseCategoryControllerTest {

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAllForAdmin_shouldReturnAllCategories_whenUserIsAdmin() throws Exception {
        CategoryResponse response = new CategoryResponse(
                1L,
                "Tables",
                "Wooden tables",
                false,
                Instant.parse("2026-01-01T10:00:00Z")
        );

        when(categoryService.findAllForAdmin()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/categories/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Tables"))
                .andExpect(jsonPath("$[0].description").value("Wooden tables"))
                .andExpect(jsonPath("$[0].active").value(false))
                .andExpect(jsonPath("$[0].createdAt").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void findAllForAdmin_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/categories/admin"))
                .andExpect(status().isForbidden());
    }
}
