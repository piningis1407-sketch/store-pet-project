package com.iharshashkou.internetstore.controller.product;

import com.iharshashkou.internetstore.DTO.category.CategoryResponse;
import com.iharshashkou.internetstore.DTO.product.ProductResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerGetByIdTest extends BaseProductControllerTest {

    @Test
    void findById_shouldReturnProduct_whenProductExists() throws Exception {
        CategoryResponse category = new CategoryResponse(
                1L,
                "Tables",
                "Wooden tables",
                true,
                Instant.parse("2026-01-01T10:00:00Z")
        );

        ProductResponse response = new ProductResponse(
                1L,
                "Epoxy Table",
                "Oak table with epoxy resin",
                new BigDecimal("1500.00"),
                5,
                "https://example.com/table.jpg",
                true,
                Instant.parse("2026-01-01T10:00:00Z"),
                category
        );

        when(productService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Epoxy Table"))
                .andExpect(jsonPath("$.description").value("Oak table with epoxy resin"))
                .andExpect(jsonPath("$.price").value(1500.00))
                .andExpect(jsonPath("$.stockQuantity").value(5))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/table.jpg"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.category.id").value(1))
                .andExpect(jsonPath("$.category.name").value("Tables"));
    }
}