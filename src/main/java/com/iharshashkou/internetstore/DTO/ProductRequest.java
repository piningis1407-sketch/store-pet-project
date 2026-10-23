package com.iharshashkou.internetstore.DTO;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 2000)
        String description,

        @NotNull
        @DecimalMin(value = "0.01")
        @Digits(integer = 10, fraction = 2)
        BigDecimal price,

        @NotNull
        @Min(0)
        Integer stockQuantity,

        @Size(max = 1000)
        String imageUrl,

        Long categoryId
) {}