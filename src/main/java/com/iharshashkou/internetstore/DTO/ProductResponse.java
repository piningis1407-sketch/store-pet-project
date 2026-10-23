package com.iharshashkou.internetstore.DTO;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String imageUrl,
        boolean active,
        Instant createdAt,
        CategoryResponse category
) {}