package com.iharshashkou.internetstore.controller.category;

import com.iharshashkou.internetstore.DTO.category.CategoryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

class CategoryControllerUpdateTest extends BaseCategoryControllerTest {

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnUpdatedCategory_whenRequestIsValid() throws Exception {
        CategoryResponse response = new CategoryResponse(
                1L,
                "New tables",
                "Updated description",
                true,
                Instant.parse("2026-01-01T10:00:00Z")
        );

        when(categoryService.update(1L, "New tables", "Updated description"))
                .thenReturn(response);

        mockMvc.perform(put("/api/categories/1")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "New tables",
                                    "description": "Updated description"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New tables"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void update_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(put("/api/categories/1")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "New tables",
                                    "description": "Updated description"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnBadRequest_whenNameIsBlank() throws Exception {
        mockMvc.perform(put("/api/categories/1")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": " ",
                                    "description": "Updated description"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnBadRequest_whenNameIsTooLong() throws Exception {
        String longName = "A".repeat(101);

        mockMvc.perform(put("/api/categories/1")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "%s",
                                    "description": "Updated description"
                                }
                                """.formatted(longName)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnBadRequest_whenDescriptionIsTooLong() throws Exception {
        String longDescription = "A".repeat(1001);

        mockMvc.perform(put("/api/categories/1")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "New tables",
                                    "description": "%s"
                                }
                                """.formatted(longDescription)))
                .andExpect(status().isBadRequest());
    }
}
