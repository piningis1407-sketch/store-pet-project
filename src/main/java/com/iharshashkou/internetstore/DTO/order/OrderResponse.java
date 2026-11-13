package com.iharshashkou.internetstore.DTO.order;

import com.iharshashkou.internetstore.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String recipientName,
        String phone,
        String deliveryAddress,
        OrderStatus status,
        BigDecimal totalPrice,
        Instant createdAt,
        List<OrderItemResponse> items
) {}
