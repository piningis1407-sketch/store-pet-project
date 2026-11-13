package com.iharshashkou.internetstore.DTO.order;

import com.iharshashkou.internetstore.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull OrderStatus status
) {}