package com.iharshashkou.internetstore.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iharshashkou.internetstore.DTO.product.ProductRequest;
import com.iharshashkou.internetstore.DTO.product.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerUpdateTest extends BaseProductControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnUpdatedProduct_whenRequestIsValid() throws Exception {
        ProductRequest request = new ProductRequest(
                "Updated Table",
                "Updated description",
                new BigDecimal("1800.00"),
                10,
                "https://example.com/updated-table.jpg",
                1L
        );

        ProductResponse response = new ProductResponse(
                1L,
                "Updated Table",
                "Updated description",
                new BigDecimal("1800.00"),
                10,
                "https://example.com/updated-table.jpg",
                true,
                Instant.parse("2026-01-01T10:00:00Z"),
                null
        );

        when(productService.update(1L, request)).thenReturn(response);

        mockMvc.perform(put("/api/products/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Table"))
                .andExpect(jsonPath("$.price").value(1800.00))
                .andExpect(jsonPath("$.stockQuantity").value(10));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnBadRequest_whenNameIsBlank() throws Exception {
        ProductRequest request = new ProductRequest(
                "",
                "Description",
                new BigDecimal("100.00"),
                5,
                null,
                null
        );

        performInvalidRequest(request);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnBadRequest_whenPriceIsZero() throws Exception {
        ProductRequest request = new ProductRequest(
                "Updated Table",
                "Description",
                BigDecimal.ZERO,
                5,
                null,
                null
        );

        performInvalidRequest(request);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnBadRequest_whenStockQuantityIsNegative() throws Exception {
        ProductRequest request = new ProductRequest(
                "Updated Table",
                "Description",
                new BigDecimal("100.00"),
                -1,
                null,
                null
        );

        performInvalidRequest(request);
    }

    @Test
    @WithMockUser(roles = "USER")
    void update_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
        ProductRequest request = new ProductRequest(
                "Updated Table",
                "Description",
                new BigDecimal("100.00"),
                5,
                null,
                null
        );

        mockMvc.perform(put("/api/products/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    private void performInvalidRequest(ProductRequest request) throws Exception {
        mockMvc.perform(put("/api/products/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}