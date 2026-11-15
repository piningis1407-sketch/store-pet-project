package com.iharshashkou.internetstore.service.customDesign;

import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.entity.customDesignEntity.*;
import com.iharshashkou.internetstore.exception.CustomDesignValidationException;
import com.iharshashkou.internetstore.repository.CategoryRepository;
import com.iharshashkou.internetstore.repository.CustomDesignRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomDesignServiceGuestAccessTest {
    @Mock
    private CustomDesignRepository customDesignRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CustomDesignService customDesignService;

    @Mock
    private Category category;


    @Test
    void getGuestDesign_shouldReturnDesignWithValidToken() {
        stubCategoryDetails();
        String token = "valid-guest-token";
        String tokenHash = hashToken(token);

        CustomDesign design = CustomDesign.builder()
                .id(5L)
                .category(category)
                .woodType(WoodType.OAK)
                .shape(DesignShape.RECTANGULAR)
                .epoxyEnabled(true)
                .epoxyColor(EpoxyColor.BLUE)
                .background(DesignBackground.INTERIOR)
                .lengthCm(120)
                .widthCm(80)
                .heightCm(75)
                .comment("Тестовый проект")
                .estimatedPrice(new BigDecimal("25000.00"))
                .status(CustomDesignStatus.GENERATED)
                .guestTokenHash(tokenHash)
                .build();

        when(customDesignRepository.findByIdAndGuestTokenHash(5L, tokenHash))
                .thenReturn(Optional.of(design));

        var response = customDesignService.getGuestDesign(5L, token);

        assertNotNull(response);
        assertEquals(5L, response.id());
        assertEquals(1L, response.categoryId());
        assertEquals("Столы", response.categoryName());
        assertEquals(CustomDesignStatus.GENERATED, response.status());

        verify(customDesignRepository)
                .findByIdAndGuestTokenHash(5L, tokenHash);
    }

    @Test
    void getGuestDesign_shouldThrowExceptionWhenTokenIsInvalid() {
        String token = "wrong-token";

        when(customDesignRepository.findByIdAndGuestTokenHash(
                5L, hashToken(token)))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> customDesignService.getGuestDesign(5L, token)
        );

        verify(customDesignRepository)
                .findByIdAndGuestTokenHash(5L, hashToken(token));
    }

    @Test
    void getGuestDesign_shouldThrowExceptionWhenTokenIsBlank() {
        assertThrows(
                CustomDesignValidationException.class,
                () -> customDesignService.getGuestDesign(5L, " ")
        );

        verifyNoInteractions(customDesignRepository);
    }

    private String hashToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private void stubCategoryDetails() {
        when(category.getId()).thenReturn(1L);
        when(category.getName()).thenReturn("Столы");
    }

}
