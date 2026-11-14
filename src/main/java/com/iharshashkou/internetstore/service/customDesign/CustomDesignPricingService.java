package com.iharshashkou.internetstore.service.customDesign;

import com.iharshashkou.internetstore.DTO.customDesign.CreateCustomDesignRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CustomDesignPricingService {

    public BigDecimal calculatePrice(CreateCustomDesignRequest request) {

        BigDecimal price = BigDecimal.valueOf(10000);

        // Порода дерева
        price = price.add(switch (request.woodType()) {
            case OAK -> BigDecimal.valueOf(5000);
            case WALNUT -> BigDecimal.valueOf(10000);
            case ASH -> BigDecimal.valueOf(4000);
            case BIRCH -> BigDecimal.valueOf(2000);
            case KARAGACH -> BigDecimal.valueOf(8000);
        });

        // Эпоксидная смола
        if (Boolean.TRUE.equals(request.epoxyEnabled())) {
            price = price.add(BigDecimal.valueOf(8000));
        }

        // Сложность формы
        price = price.add(switch (request.shape()) {
            case ROUND -> BigDecimal.valueOf(3000);
            case RECTANGULAR -> BigDecimal.ZERO;
            case OVAL -> BigDecimal.valueOf(4000);
            case SQUARE -> BigDecimal.valueOf(1000);
            case IRREGULAR -> BigDecimal.valueOf(7000);
        });

        // Предварительная оценка
        return price.setScale(2, RoundingMode.HALF_UP);
    }
}
