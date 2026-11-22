package com.iharshashkou.internetstore.controller.cart;

import com.iharshashkou.internetstore.DTO.cart.CartResponse;
import com.iharshashkou.internetstore.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

class CartControllerUpdateItemTest extends BaseCartControllerTest {

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void updateItem_shouldReturnUpdatedCart_whenRequestIsValid() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        CartResponse response = new CartResponse(
                List.of(),
                3,
                new BigDecimal("750.00")
        );

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        when(cartService.updateItem(1L, 10L, 3))
                .thenReturn(response);

        mockMvc.perform(patch("/api/cart/items/10")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "quantity": 3
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(3))
                .andExpect(jsonPath("$.totalPrice").value(750.00));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void updateItem_shouldReturnBadRequest_whenQuantityIsNull() throws Exception {
        mockMvc.perform(patch("/api/cart/items/10")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "quantity": null
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void updateItem_shouldReturnBadRequest_whenQuantityIsNotPositive() throws Exception {
        mockMvc.perform(patch("/api/cart/items/10")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                    "quantity": 0
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}