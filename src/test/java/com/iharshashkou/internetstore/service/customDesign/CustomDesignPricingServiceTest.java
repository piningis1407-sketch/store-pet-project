package com.iharshashkou.internetstore.service.customDesign;

import com.iharshashkou.internetstore.DTO.customDesign.CreateCustomDesignRequest;
import com.iharshashkou.internetstore.entity.customDesignEntity.DesignBackground;
import com.iharshashkou.internetstore.entity.customDesignEntity.DesignShape;
import com.iharshashkou.internetstore.entity.customDesignEntity.WoodType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomDesignPricingServiceTest {

    private final CustomDesignPricingService pricingService =
            new CustomDesignPricingService();

    @Test
    void calculatesPriceForEveryWoodType() {

        Map<WoodType, Long> expectedPrices = Map.of(
                WoodType.OAK, 15000L,
                WoodType.WALNUT, 20000L,
                WoodType.ASH, 14000L,
                WoodType.BIRCH, 12000L,
                WoodType.KARAGACH, 18000L
        );

        expectedPrices.forEach((woodType, expectedPrice) -> {

            CreateCustomDesignRequest request = createRequest(
                    woodType,
                    DesignShape.RECTANGULAR,
                    false
            );

            assertEquals(
                    BigDecimal.valueOf(expectedPrice).setScale(2),
                    pricingService.calculatePrice(request),
                    "Incorrect price for wood type: " + woodType
            );
        });
    }

    @Test
    void calculatesPriceForEveryShape() {

        Map<DesignShape, Long> expectedPrices = Map.of(
                DesignShape.ROUND, 18000L,
                DesignShape.RECTANGULAR, 15000L,
                DesignShape.OVAL, 19000L,
                DesignShape.SQUARE, 16000L,
                DesignShape.IRREGULAR, 22000L
        );

        expectedPrices.forEach((shape, expectedPrice) -> {

            CreateCustomDesignRequest request = createRequest(
                    WoodType.OAK,
                    shape,
                    false
            );

            assertEquals(
                    BigDecimal.valueOf(expectedPrice).setScale(2),
                    pricingService.calculatePrice(request),
                    "Incorrect price for shape: " + shape
            );
        });
    }

    @Test
    void addsEpoxyPriceOnlyWhenEnabled() {

        CreateCustomDesignRequest withoutEpoxy = createRequest(
                WoodType.OAK,
                DesignShape.RECTANGULAR,
                false
        );

        CreateCustomDesignRequest withEpoxy = createRequest(
                WoodType.OAK,
                DesignShape.RECTANGULAR,
                true
        );

        assertEquals(
                new BigDecimal("15000.00"),
                pricingService.calculatePrice(withoutEpoxy)
        );

        assertEquals(
                new BigDecimal("23000.00"),
                pricingService.calculatePrice(withEpoxy)
        );
    }

    private CreateCustomDesignRequest createRequest(
            WoodType woodType,
            DesignShape shape,
            Boolean epoxyEnabled
    ) {
        return new CreateCustomDesignRequest(
                1L,
                woodType,
                shape,
                epoxyEnabled,
                null,
                DesignBackground.NEUTRAL,
                100,
                60,
                5,
                null,
                null
        );
    }
}
