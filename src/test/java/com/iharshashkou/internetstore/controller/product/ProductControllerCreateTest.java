package com.iharshashkou.internetstore.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iharshashkou.internetstore.DTO.product.ProductRequest;
import com.iharshashkou.internetstore.DTO.product.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerCreateTest extends BaseProductControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturnCreated_whenRequestIsValid() throws Exception {
        ProductRequest request = new ProductRequest(
                "Epoxy Table",
                "Oak table with epoxy resin",
                new BigDecimal("1500.00"),
                5,
                "https://example.com/table.jpg",
                1L
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
                null
        );

        when(productService.create(request)).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Epoxy Table"))
                .andExpect(jsonPath("$.price").value(1500.00))
                .andExpect(jsonPath("$.stockQuantity").value(5))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturnBadRequest_whenNameIsBlank() throws Exception {
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
    void create_shouldReturnBadRequest_whenPriceIsZero() throws Exception {
        ProductRequest request = new ProductRequest(
                "Epoxy Table",
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
    void create_shouldReturnBadRequest_whenStockQuantityIsNegative() throws Exception {
        ProductRequest request = new ProductRequest(
                "Epoxy Table",
                "Description",
                new BigDecimal("100.00"),
                -1,
                null,
                null
        );

        performInvalidRequest(request);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturnBadRequest_whenNameIsTooLong() throws Exception {
        ProductRequest request = new ProductRequest(
                "A".repeat(151),
                "Description",
                new BigDecimal("100.00"),
                5,
                null,
                null
        );

        performInvalidRequest(request);
    }

    @Test
    @WithMockUser(roles = "USER")
    void create_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
        ProductRequest request = new ProductRequest(
                "Epoxy Table",
                "Description",
                new BigDecimal("100.00"),
                5,
                null,
                null
        );

        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_shouldReturnUnauthorized_whenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Oak Table",
                                "description": "Solid oak table",
                                "price": 1500.00,
                                "stockQuantity": 5,
                                "imageUrl": "https://example.com/table.jpg",
                                "categoryId": 1
                            }
                            """))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(productService);
    }

    private void performInvalidRequest(ProductRequest request) throws Exception {
        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
