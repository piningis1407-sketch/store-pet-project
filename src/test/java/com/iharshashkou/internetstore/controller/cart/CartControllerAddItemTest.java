package com.iharshashkou.internetstore.controller.cart;

import com.iharshashkou.internetstore.DTO.cart.CartResponse;
import com.iharshashkou.internetstore.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CartControllerAddItemTest extends BaseCartControllerTest {

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void addItem_shouldReturnUpdatedCart_whenRequestIsValid() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        CartResponse response = new CartResponse(
                List.of(),
                2,
                new BigDecimal("500.00")
        );

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        when(cartService.addItem(1L, 10L, 2))
                .thenReturn(response);

        mockMvc.perform(post("/api/cart/items")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "productId": 10,
                                    "quantity": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.totalPrice").value(500.00));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void addItem_shouldReturnBadRequest_whenProductIdIsNull() throws Exception {
        mockMvc.perform(post("/api/cart/items")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "productId": null,
                                    "quantity": 2
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void addItem_shouldReturnBadRequest_whenProductIdIsNotPositive() throws Exception {
        mockMvc.perform(post("/api/cart/items")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "productId": 0,
                                    "quantity": 2
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void addItem_shouldReturnBadRequest_whenQuantityIsNull() throws Exception {
        mockMvc.perform(post("/api/cart/items")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "productId": 10,
                                    "quantity": null
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void addItem_shouldReturnBadRequest_whenQuantityIsNotPositive() throws Exception {
        mockMvc.perform(post("/api/cart/items")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "productId": 10,
                                    "quantity": -1
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
