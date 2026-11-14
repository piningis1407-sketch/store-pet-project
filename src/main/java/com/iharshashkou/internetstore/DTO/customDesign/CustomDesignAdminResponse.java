package com.iharshashkou.internetstore.DTO.customDesign;

import com.iharshashkou.internetstore.entity.customDesignEntity.*;

import java.math.BigDecimal;
import java.time.Instant;

public record CustomDesignAdminResponse(

        Long id,

        Long userId,

        String userName,

        String userEmail,

        Long categoryId,

        String categoryName,

        WoodType woodType,

        DesignShape shape,

        boolean epoxyEnabled,

        EpoxyColor epoxyColor,

        DesignBackground background,

        Integer lengthCm,

        Integer widthCm,

        Integer heightCm,

        Integer diameterCm,

        String comment,

        String adminComment,

        String imageUrl,

        BigDecimal estimatedPrice,

        BigDecimal finalPrice,

        CustomDesignStatus status,

        Instant createdAt

) {}
