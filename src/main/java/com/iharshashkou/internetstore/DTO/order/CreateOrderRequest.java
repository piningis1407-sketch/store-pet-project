package com.iharshashkou.internetstore.DTO.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(

        @NotBlank
        @Size(max = 30)
        String recipientName,

        @NotBlank
        @Size(max = 30)
        String phone,

        @NotBlank
        @Size(max = 500)
        String deliveryAddress

) {}
