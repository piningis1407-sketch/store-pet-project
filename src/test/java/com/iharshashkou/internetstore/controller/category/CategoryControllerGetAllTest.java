package com.iharshashkou.internetstore.controller.category;

import com.iharshashkou.internetstore.DTO.category.CategoryResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryControllerGetAllTest extends BaseCategoryControllerTest {

    @Test
    void findAll_shouldReturnCategories() throws Exception {
        CategoryResponse response = new CategoryResponse(
                1L,
                "Tables",
                "Wooden tables",
                true,
                Instant.parse("2026-01-01T10:00:00Z")
        );

        when(categoryService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Tables"))
                .andExpect(jsonPath("$[0].description").value("Wooden tables"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[0].createdAt").exists());
    }
}
