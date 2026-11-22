package com.iharshashkou.internetstore.controller.order;

import com.iharshashkou.internetstore.DTO.order.OrderResponse;
import com.iharshashkou.internetstore.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerGetAllOrdersTest extends BaseOrderControllerTest {

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getAllOrders_shouldReturnOrders_whenAdminIsAuthenticated() throws Exception {
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

        when(orderService.getAllOrders())
                .thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("NEW"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getAllOrders_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/orders/admin"))
                .andExpect(status().isForbidden());
    }
}
