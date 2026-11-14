package com.iharshashkou.internetstore.DTO.customDesign;

import com.iharshashkou.internetstore.entity.customDesignEntity.CustomDesignStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ReviewCustomDesignRequest(

        @NotNull
        CustomDesignStatus status,

        @DecimalMin("0.01")
        @Digits(integer = 10, fraction = 2)
        BigDecimal finalPrice,

        @Size(max = 2000)
        String adminComment

) {}
