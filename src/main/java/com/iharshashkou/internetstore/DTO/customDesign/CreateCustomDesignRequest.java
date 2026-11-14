package com.iharshashkou.internetstore.DTO.customDesign;

import com.iharshashkou.internetstore.entity.customDesignEntity.DesignBackground;
import com.iharshashkou.internetstore.entity.customDesignEntity.DesignShape;
import com.iharshashkou.internetstore.entity.customDesignEntity.EpoxyColor;
import com.iharshashkou.internetstore.entity.customDesignEntity.WoodType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCustomDesignRequest(

        @NotNull
        Long categoryId,

        @NotNull
        WoodType woodType,

        @NotNull
        DesignShape shape,

        @NotNull
        Boolean epoxyEnabled,

        EpoxyColor epoxyColor,

        @NotNull
        DesignBackground background,

        @Min(20)
        @Max(2000)
        Integer lengthCm,

        @Min(20)
        @Max(1000)
        Integer widthCm,

        @Min(2)
        @Max(1000)
        Integer heightCm,

        @Min(20)
        @Max(1000)
        Integer diameterCm,

        @Size(max = 2000)
        String comment

) {}
