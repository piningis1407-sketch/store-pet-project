package com.iharshashkou.internetstore.DTO;

import java.math.BigDecimal;

public record CartItemResponse(
        Long productId,
        String name,
        BigDecimal price,
        String imageUrl,
        Integer stockQuantity,
        Integer quantity,
        BigDecimal subtotal
) {}
