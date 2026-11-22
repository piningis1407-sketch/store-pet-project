package com.iharshashkou.internetstore.controller.order;

import com.iharshashkou.internetstore.DTO.order.OrderResponse;
import com.iharshashkou.internetstore.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerUpdateStatusTest extends BaseOrderControllerTest {

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void updateOrderStatus_shouldUpdateStatus_whenAdminIsAuthenticated() throws Exception {
        OrderResponse response = new OrderResponse(
                10L,
                "Ivan Ivanov",
                "+123456789",
                "Main Street 10",
                OrderStatus.SHIPPED,
                new BigDecimal("500.00"),
                Instant.parse("2026-11-21T10:00:00Z"),
                List.of()
        );

        when(orderService.updateOrderStatus(10L, OrderStatus.SHIPPED))
                .thenReturn(response);

        mockMvc.perform(patch("/api/orders/admin/10/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "SHIPPED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void updateOrderStatus_shouldReturnForbidden_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(patch("/api/orders/admin/10/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "SHIPPED"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void updateOrderStatus_shouldReturnBadRequest_whenStatusIsNull() throws Exception {
        mockMvc.perform(patch("/api/orders/admin/10/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": null
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void updateOrderStatus_shouldReturnBadRequest_whenStatusIsInvalid() throws Exception {
        mockMvc.perform(patch("/api/orders/admin/10/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "INVALID"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
