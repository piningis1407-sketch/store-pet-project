package com.iharshashkou.internetstore.controller.order;

import com.iharshashkou.internetstore.DTO.order.OrderResponse;
import com.iharshashkou.internetstore.entity.OrderStatus;
import com.iharshashkou.internetstore.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerCreateTest extends BaseOrderControllerTest {

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void createOrder_shouldReturnCreatedOrder_whenRequestIsValid() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        OrderResponse response = new OrderResponse(
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

        when(orderService.createOrder(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "recipientName": "Ivan Ivanov",
                                  "phone": "+123456789",
                                  "deliveryAddress": "Main Street 10"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.recipientName").value("Ivan Ivanov"))
                .andExpect(jsonPath("$.phone").value("+123456789"))
                .andExpect(jsonPath("$.deliveryAddress").value("Main Street 10"))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.totalPrice").value(500.00));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void createOrder_shouldReturnBadRequest_whenRecipientNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "recipientName": "",
                                  "phone": "+123456789",
                                  "deliveryAddress": "Main Street 10"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void createOrder_shouldReturnBadRequest_whenPhoneIsBlank() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "recipientName": "Ivan Ivanov",
                                  "phone": "",
                                  "deliveryAddress": "Main Street 10"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void createOrder_shouldReturnBadRequest_whenDeliveryAddressIsBlank() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "recipientName": "Ivan Ivanov",
                                  "phone": "+123456789",
                                  "deliveryAddress": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
