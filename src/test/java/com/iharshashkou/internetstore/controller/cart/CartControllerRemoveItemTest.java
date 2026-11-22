package com.iharshashkou.internetstore.controller.cart;

import com.iharshashkou.internetstore.DTO.cart.CartResponse;
import com.iharshashkou.internetstore.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

class CartControllerRemoveItemTest extends BaseCartControllerTest {

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void removeItem_shouldReturnUpdatedCart_whenUserIsAuthenticated() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        CartResponse response = new CartResponse(
                List.of(),
                0,
                BigDecimal.ZERO
        );

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        when(cartService.removeItem(1L, 10L))
                .thenReturn(response);

        mockMvc.perform(delete("/api/cart/items/10")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(0))
                .andExpect(jsonPath("$.totalPrice").value(0));
    }
}
