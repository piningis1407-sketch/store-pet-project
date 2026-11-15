package com.iharshashkou.internetstore.service.customDesign;

import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.entity.CategoryType;
import com.iharshashkou.internetstore.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import com.iharshashkou.internetstore.DTO.customDesign.CreateCustomDesignRequest;
import com.iharshashkou.internetstore.entity.customDesignEntity.DesignBackground;
import com.iharshashkou.internetstore.entity.customDesignEntity.DesignShape;
import com.iharshashkou.internetstore.entity.customDesignEntity.WoodType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CustomDesignPricingServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CustomDesignPricingService pricingService;

    @BeforeEach
    void setUp() {

        pricingService = new CustomDesignPricingService(categoryRepository);

        Category category = Category.builder()
                .id(1L)
                .name("Столы")
                .type(CategoryType.TABLE)
                .build();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));
    }

    @Test
    void calculatesPriceForEveryWoodType() {

        Map<WoodType, BigDecimal> expectedPrices = Map.of(
                WoodType.OAK, new BigDecimal("2625.00"),
                WoodType.WALNUT, new BigDecimal("3491.25"),
                WoodType.ASH, new BigDecimal("2441.25"),
                WoodType.BIRCH, new BigDecimal("2100.00"),
                WoodType.KARAGACH, new BigDecimal("3150.00")
        );

        expectedPrices.forEach((woodType, expectedPrice) -> {

            CreateCustomDesignRequest request = createRequest(
                    woodType,
                    DesignShape.RECTANGULAR,
                    false
            );

            assertEquals(
                    expectedPrice,
                    pricingService.calculatePrice(request),
                    "Incorrect price for wood type: " + woodType
            );
        });
    }

    @Test
    void calculatesPriceForEveryShape() {

        Map<DesignShape, BigDecimal> expectedPrices = Map.of(
                DesignShape.ROUND, new BigDecimal("4123.34"),
                DesignShape.RECTANGULAR, new BigDecimal("2625.00"),
                DesignShape.OVAL, new BigDecimal("2618.32"),
                DesignShape.SQUARE, new BigDecimal("2808.75"),
                DesignShape.IRREGULAR, new BigDecimal("3858.75")
        );

        expectedPrices.forEach((shape, expectedPrice) -> {

            CreateCustomDesignRequest request = createRequest(
                    WoodType.OAK,
                    shape,
                    false
            );

            assertEquals(
                    expectedPrice,
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
                new BigDecimal("2625.00"),
                pricingService.calculatePrice(withoutEpoxy)
        );

        assertEquals(
                new BigDecimal("3825.00"),
                pricingService.calculatePrice(withEpoxy)
        );
    }

    private CreateCustomDesignRequest createRequest(
            WoodType woodType,
            DesignShape shape,
            Boolean epoxyEnabled
    ) {
        Integer length = shape == DesignShape.ROUND ? null : 100;
        Integer width = shape == DesignShape.ROUND ? null : 60;
        Integer diameter = shape == DesignShape.ROUND ? 100 : null;

        return new CreateCustomDesignRequest(
                1L,
                woodType,
                shape,
                epoxyEnabled,
                null,
                DesignBackground.NEUTRAL,
                length,
                width,
                5,
                diameter,
                null
        );
    }
}
