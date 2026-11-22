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

class OrderControllerGetUserOrdersTest extends BaseOrderControllerTest {

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getUserOrders_shouldReturnOrders_whenUserIsAuthenticated() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        OrderResponse order = new OrderResponse(
                1L,
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

        when(orderService.getUserOrders(1L))
                .thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].recipientName").value("Ivan Ivanov"))
                .andExpect(jsonPath("$[0].status").value("NEW"))
                .andExpect(jsonPath("$[0].totalPrice").value(500.00));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getUserOrders_shouldReturnEmptyList_whenUserHasNoOrders() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        when(orderService.getUserOrders(1L))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
