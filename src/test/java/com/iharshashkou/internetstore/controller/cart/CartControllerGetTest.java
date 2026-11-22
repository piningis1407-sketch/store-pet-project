package com.iharshashkou.internetstore.controller.cart;

import com.iharshashkou.internetstore.DTO.cart.CartItemResponse;
import com.iharshashkou.internetstore.DTO.cart.CartResponse;
import com.iharshashkou.internetstore.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CartControllerGetTest extends BaseCartControllerTest {

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getCart_shouldReturnCart_whenUserIsAuthenticated() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        CartItemResponse item = new CartItemResponse(
                10L,
                "Oak Table",
                new BigDecimal("250.00"),
                "https://example.com/table.jpg",
                5,
                2,
                new BigDecimal("500.00")
        );

        CartResponse response = new CartResponse(
                List.of(item),
                2,
                new BigDecimal("500.00")
        );

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        when(cartService.getCart(1L)).thenReturn(response);

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.totalPrice").value(500.00))
                .andExpect(jsonPath("$.items[0].productId").value(10))
                .andExpect(jsonPath("$.items[0].name").value("Oak Table"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].subtotal").value(500.00));
    }

    @Test
    @WithMockUser(username = "unknown@example.com", roles = "USER")
    void getCart_shouldThrowException_whenUserDoesNotExist() throws Exception {
        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isNotFound());
    }
}
