package com.iharshashkou.internetstore.controller.product;

import com.iharshashkou.internetstore.DTO.category.CategoryResponse;
import com.iharshashkou.internetstore.DTO.product.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.Mockito.*;
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

    @Test
    void findById_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
        when(productService.findById(999L))
                .thenThrow(new EntityNotFoundException("Product not found: 999"));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Product not found: 999"));

        verify(productService).findById(999L);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void findById_shouldBeAccessibleWithoutAuthentication() throws Exception {
        ProductResponse response = new ProductResponse(
                1L,
                "Epoxy Table",
                "Oak table with epoxy resin",
                new BigDecimal("1500.00"),
                5,
                "https://example.com/table.jpg",
                true,
                Instant.parse("2026-01-01T10:00:00Z"),
                null
        );

        when(productService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Epoxy Table"));

        verify(productService).findById(1L);
    }

    @Test
    void findById_shouldReturnNotFound_whenProductIsInactive() throws Exception {
        when(productService.findById(2L))
                .thenThrow(new EntityNotFoundException("Product not found: 2"));

        mockMvc.perform(get("/api/products/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found: 2"));

        verify(productService).findById(2L);
    }
}