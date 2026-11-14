package com.iharshashkou.internetstore.service.customDesign;

import com.iharshashkou.internetstore.DTO.customDesign.CreateCustomDesignRequest;
import com.iharshashkou.internetstore.exception.CustomDesignValidationException;
import org.springframework.stereotype.Service;

@Service
public class CustomDesignValidationService {

    public void validateDimensions(CreateCustomDesignRequest request) {

        boolean valid = switch (request.shape()) {

            case ROUND ->
                    request.diameterCm() != null
                            && request.lengthCm() == null
                            && request.widthCm() == null;

            case RECTANGULAR, SQUARE, OVAL ->
                    request.lengthCm() != null
                            && request.widthCm() != null
                            && request.diameterCm() == null;

            case IRREGULAR ->
                    request.lengthCm() != null
                            && request.widthCm() != null
                            && request.diameterCm() == null;
        };

        if (!valid) {
            throw new IllegalArgumentException(
                    "Размеры не соответствуют выбранной форме изделия"
            );
        }

        if (request.heightCm() == null) {
            throw new CustomDesignValidationException(
                    "Необходимо указать высоту изделия"
            );
        }

        if (!Boolean.TRUE.equals(request.epoxyEnabled())
                && request.epoxyColor() != null) {
            throw new CustomDesignValidationException(
                    "Нельзя указывать цвет смолы, если она не используется"
            );
        }

        if (Boolean.TRUE.equals(request.epoxyEnabled())
                && request.epoxyColor() == null) {
            throw new CustomDesignValidationException(
                    "Необходимо выбрать цвет эпоксидной смолы"
            );
        }
    }
}
