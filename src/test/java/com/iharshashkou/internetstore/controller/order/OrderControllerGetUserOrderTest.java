package com.iharshashkou.internetstore.controller.order;

import com.iharshashkou.internetstore.DTO.order.OrderResponse;
import com.iharshashkou.internetstore.entity.OrderStatus;
import com.iharshashkou.internetstore.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerGetUserOrderTest extends BaseOrderControllerTest {

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getUserOrder_shouldReturnOrder_whenOrderExists() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        OrderResponse response = new OrderResponse(
                10L,
                "Ivan Ivanov",
                "+123456789",
                "Main Street 10",
                OrderStatus.NEW,
                new BigDecimal("500.00"),
                Instant.parse("2026-11-21T10:00:00Z"),
                List.of()
        );

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        when(orderService.getUserOrder(1L, 10L))
                .thenReturn(response);

        mockMvc.perform(get("/api/orders/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.recipientName").value("Ivan Ivanov"))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.totalPrice").value(500.00));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getUserOrder_shouldReturnNotFound_whenOrderDoesNotExist() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        when(orderService.getUserOrder(1L, 999L))
                .thenThrow(new jakarta.persistence.EntityNotFoundException("Заказ не найден"));

        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound());
    }
}
