package com.iharshashkou.internetstore.controller.category;

import com.iharshashkou.internetstore.DTO.category.CategoryResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryControllerGetByIdTest extends BaseCategoryControllerTest {

    @Test
    void findById_shouldReturnCategory_whenCategoryExists() throws Exception {
        CategoryResponse response = new CategoryResponse(
                1L,
                "Tables",
                "Wooden tables",
                true,
                Instant.parse("2026-01-01T10:00:00Z")
        );

        when(categoryService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Tables"))
                .andExpect(jsonPath("$.description").value("Wooden tables"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.createdAt").exists());
    }
}
