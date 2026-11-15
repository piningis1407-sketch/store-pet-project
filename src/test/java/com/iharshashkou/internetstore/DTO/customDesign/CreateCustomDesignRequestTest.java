package com.iharshashkou.internetstore.DTO.customDesign;

import com.iharshashkou.internetstore.entity.customDesignEntity.DesignBackground;
import com.iharshashkou.internetstore.entity.customDesignEntity.DesignShape;
import com.iharshashkou.internetstore.entity.customDesignEntity.WoodType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateCustomDesignRequestTest {

    private final Validator validator =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsMinimumAndMaximumAllowedDimensions() {

        CreateCustomDesignRequest request = createRequest(
                DesignShape.RECTANGULAR,
                2000,
                1000,
                2,
                null,
                null
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsDimensionsBelowMinimum() {

        CreateCustomDesignRequest request = createRequest(
                DesignShape.RECTANGULAR,
                19,
                19,
                1,
                null,
                null
        );

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsDimensionsAboveMaximum() {

        CreateCustomDesignRequest request = createRequest(
                DesignShape.RECTANGULAR,
                2001,
                1001,
                1001,
                null,
                null
        );

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void validatesDiameterBoundaries() {

        CreateCustomDesignRequest validRequest = createRequest(
                DesignShape.ROUND,
                null,
                null,
                2,
                20,
                null
        );

        CreateCustomDesignRequest invalidRequest = createRequest(
                DesignShape.ROUND,
                null,
                null,
                2,
                1001,
                null
        );

        assertTrue(validator.validate(validRequest).isEmpty());
        assertFalse(validator.validate(invalidRequest).isEmpty());
    }

    @Test
    void rejectsMissingRequiredFields() {

        CreateCustomDesignRequest request = new CreateCustomDesignRequest(
                null,
                null,
                null,
                null,
                null,
                null,
                100,
                60,
                5,
                null,
                null
        );

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void acceptsCommentWithMaximumLength() {

        CreateCustomDesignRequest request = createRequest(
                DesignShape.RECTANGULAR,
                100,
                60,
                5,
                null,
                "a".repeat(2000)
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsCommentExceedingMaximumLength() {

        CreateCustomDesignRequest request = createRequest(
                DesignShape.RECTANGULAR,
                100,
                60,
                5,
                null,
                "a".repeat(2001)
        );

        assertFalse(validator.validate(request).isEmpty());
    }

    private CreateCustomDesignRequest createRequest(
            DesignShape shape,
            Integer length,
            Integer width,
            Integer height,
            Integer diameter,
            String comment
    ) {
        return new CreateCustomDesignRequest(
                1L,
                WoodType.OAK,
                shape,
                false,
                null,
                DesignBackground.NEUTRAL,
                length,
                width,
                height,
                diameter,
                comment
        );
    }
}
