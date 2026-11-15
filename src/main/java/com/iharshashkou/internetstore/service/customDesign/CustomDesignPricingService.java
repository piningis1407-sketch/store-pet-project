package com.iharshashkou.internetstore.service.customDesign;

import com.iharshashkou.internetstore.entity.CategoryType;
import com.iharshashkou.internetstore.DTO.customDesign.CreateCustomDesignRequest;
import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CustomDesignPricingService {

    private final CategoryRepository categoryRepository;

    private static final BigDecimal BASE_PRICE_PER_M2 =
            BigDecimal.valueOf(3500);

    private static final BigDecimal EPOXY_PRICE_PER_M2 =
            BigDecimal.valueOf(2000);

    private static final BigDecimal STANDARD_THICKNESS_CM =
            BigDecimal.valueOf(4);

    private static final BigDecimal CM2_PER_M2 =
            BigDecimal.valueOf(10000);

    private static final BigDecimal PI =
            new BigDecimal("3.14159265358979323846");

    public BigDecimal calculatePrice(CreateCustomDesignRequest request) {

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category not found: " + request.categoryId()
                ));

        BigDecimal categoryCoefficient = switch (category.getType()) {
            case TABLE -> BigDecimal.valueOf(1.00);
            case CHAIR -> BigDecimal.valueOf(1.50);
            case OTHER -> BigDecimal.valueOf(1.15);
        };

        return calculateProductPrice(request, categoryCoefficient);
    }

    private BigDecimal calculateProductPrice(
            CreateCustomDesignRequest request,
            BigDecimal categoryCoefficient
    ) {
        // Площадь изделия в квадратных метрах
        BigDecimal area = calculateArea(request);

        // Коэффициент толщины относительно стандартных 4 см
        BigDecimal thicknessCoefficient =
                BigDecimal.valueOf(request.heightCm())
                        .divide(STANDARD_THICKNESS_CM, 8, RoundingMode.HALF_UP);

        // Коэффициент породы дерева
        BigDecimal woodCoefficient = switch (request.woodType()) {
            case OAK -> BigDecimal.valueOf(1.00);
            case WALNUT -> BigDecimal.valueOf(1.33);
            case ASH -> BigDecimal.valueOf(0.93);
            case BIRCH -> BigDecimal.valueOf(0.80);
            case KARAGACH -> BigDecimal.valueOf(1.20);
        };

        // Коэффициент формы
        BigDecimal shapeCoefficient = switch (request.shape()) {
            case RECTANGULAR -> BigDecimal.valueOf(1.00);
            case ROUND -> BigDecimal.valueOf(1.20);
            case OVAL -> BigDecimal.valueOf(1.27);
            case SQUARE -> BigDecimal.valueOf(1.07);
            case IRREGULAR -> BigDecimal.valueOf(1.47);
        };

        // Базовая стоимость изделия
        BigDecimal price = area
                .multiply(BASE_PRICE_PER_M2)
                .multiply(thicknessCoefficient)
                .multiply(woodCoefficient)
                .multiply(shapeCoefficient)
                .multiply(categoryCoefficient);

        // Доплата за эпоксидную смолу
        if (Boolean.TRUE.equals(request.epoxyEnabled())) {
            price = price.add(area.multiply(EPOXY_PRICE_PER_M2));
        }

        return price.setScale(2, RoundingMode.HALF_UP);
    }
    private BigDecimal calculateArea(CreateCustomDesignRequest request) {

        BigDecimal areaCm2;

        switch (request.shape()) {

            case ROUND -> {
                BigDecimal diameter =
                        BigDecimal.valueOf(request.diameterCm());

                areaCm2 = PI.multiply(diameter.multiply(diameter))
                        .divide(BigDecimal.valueOf(4), 10, RoundingMode.HALF_UP);
            }

            case OVAL -> {
                BigDecimal length = BigDecimal.valueOf(request.lengthCm());
                BigDecimal width = BigDecimal.valueOf(request.widthCm());

                areaCm2 = PI.multiply(length).multiply(width)
                        .divide(BigDecimal.valueOf(4), 10, RoundingMode.HALF_UP);
            }

            case RECTANGULAR, SQUARE, IRREGULAR -> {
                BigDecimal length = BigDecimal.valueOf(request.lengthCm());
                BigDecimal width = BigDecimal.valueOf(request.widthCm());

                areaCm2 = length.multiply(width);
            }

            default -> throw new IllegalArgumentException(
                    "Неизвестная форма изделия"
            );
        }

        return areaCm2.divide(CM2_PER_M2, 10, RoundingMode.HALF_UP);
    }
}