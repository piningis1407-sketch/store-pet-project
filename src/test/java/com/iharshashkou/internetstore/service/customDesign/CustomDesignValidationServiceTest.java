package com.iharshashkou.internetstore.service.customDesign;

import com.iharshashkou.internetstore.DTO.customDesign.CreateCustomDesignRequest;
import com.iharshashkou.internetstore.entity.customDesignEntity.DesignBackground;
import com.iharshashkou.internetstore.entity.customDesignEntity.DesignShape;
import com.iharshashkou.internetstore.entity.customDesignEntity.EpoxyColor;
import com.iharshashkou.internetstore.entity.customDesignEntity.WoodType;
import com.iharshashkou.internetstore.exception.CustomDesignValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomDesignValidationServiceTest {

    private final CustomDesignValidationService validationService =
            new CustomDesignValidationService();

    @Test
    void acceptsValidDimensionsForEveryShape() {

        for (DesignShape shape : DesignShape.values()) {
            CreateCustomDesignRequest request = createRequest(
                    shape, true, EpoxyColor.BLUE,
                    shape == DesignShape.ROUND ? null : 100,
                    shape == DesignShape.ROUND ? null : 60,
                    shape == DesignShape.ROUND ? 80 : null,
                    5
            );

            assertDoesNotThrow(
                    () -> validationService.validateDimensions(request),
                    "Valid dimensions rejected for shape: " + shape
            );
        }
    }

    @Test
    void rejectsRoundShapeWithoutDiameter() {

        CreateCustomDesignRequest request = createRequest(
                DesignShape.ROUND, false, null,
                null, null, null, 5
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> validationService.validateDimensions(request)
        );
    }

    @Test
    void rejectsRectangularShapeWithoutLength() {

        CreateCustomDesignRequest request = createRequest(
                DesignShape.RECTANGULAR, false, null,
                null, 60, null, 5
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> validationService.validateDimensions(request)
        );
    }

    @Test
    void rejectsRoundShapeWithLengthAndWidth() {

        CreateCustomDesignRequest request = createRequest(
                DesignShape.ROUND, false, null,
                100, 60, 80, 5
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> validationService.validateDimensions(request)
        );
    }

    @Test
    void rejectsRequestWithoutHeight() {

        CreateCustomDesignRequest request = createRequest(
                DesignShape.RECTANGULAR, false, null,
                100, 60, null, null
        );

        assertThrows(
                CustomDesignValidationException.class,
                () -> validationService.validateDimensions(request)
        );
    }

    @Test
    void rejectsEpoxyColorWhenEpoxyIsDisabled() {

        CreateCustomDesignRequest request = createRequest(
                DesignShape.RECTANGULAR, false, EpoxyColor.BLUE,
                100, 60, null, 5
        );

        assertThrows(
                CustomDesignValidationException.class,
                () -> validationService.validateDimensions(request)
        );
    }

    @Test
    void rejectsMissingEpoxyColorWhenEpoxyIsEnabled() {

        CreateCustomDesignRequest request = createRequest(
                DesignShape.RECTANGULAR, true, null,
                100, 60, null, 5
        );

        assertThrows(
                CustomDesignValidationException.class,
                () -> validationService.validateDimensions(request)
        );
    }

    @Test
    void acceptsRequestWithoutEpoxyAndWithoutColor() {

        CreateCustomDesignRequest request = createRequest(
                DesignShape.RECTANGULAR, false, null,
                100, 60, null, 5
        );

        assertDoesNotThrow(
                () -> validationService.validateDimensions(request)
        );
    }

    private CreateCustomDesignRequest createRequest(
            DesignShape shape,
            Boolean epoxyEnabled,
            EpoxyColor epoxyColor,
            Integer length,
            Integer width,
            Integer diameter,
            Integer height
    ) {
        return new CreateCustomDesignRequest(
                1L,
                WoodType.OAK,
                shape,
                epoxyEnabled,
                epoxyColor,
                DesignBackground.NEUTRAL,
                length,
                width,
                height,
                diameter,
                null
        );
    }
}
