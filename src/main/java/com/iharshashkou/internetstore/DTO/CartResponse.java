package com.iharshashkou.internetstore.DTO;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        List<CartItemResponse> items,
        Integer totalItems,
        BigDecimal totalPrice
) {}