package com.iharshashkou.internetstore.controller.category;

import com.iharshashkou.internetstore.DTO.category.CategoryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

class CategoryControllerCreateTest extends BaseCategoryControllerTest {

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturnCreated_whenRequestIsValid() throws Exception {
        CategoryResponse response = new CategoryResponse(
                1L,
                "Tables",
                "Wooden tables",
                true,
                Instant.parse("2026-01-01T10:00:00Z")
        );

        when(categoryService.create("Tables", "Wooden tables"))
                .thenReturn(response);

        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "Tables",
                                    "description": "Wooden tables"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Tables"))
                .andExpect(jsonPath("$.description").value("Wooden tables"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void create_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "Tables",
                                    "description": "Wooden tables"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturnBadRequest_whenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": " ",
                                    "description": "Wooden tables"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturnBadRequest_whenNameIsTooLong() throws Exception {
        String longName = "A".repeat(101);

        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "%s",
                                    "description": "Wooden tables"
                                }
                                """.formatted(longName)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturnBadRequest_whenDescriptionIsTooLong() throws Exception {
        String longDescription = "A".repeat(1001);

        mockMvc.perform(post("/api/categories")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": "Tables",
                                    "description": "%s"
                                }
                                """.formatted(longDescription)))
                .andExpect(status().isBadRequest());
    }
}
