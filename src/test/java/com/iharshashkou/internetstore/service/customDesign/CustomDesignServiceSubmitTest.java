package com.iharshashkou.internetstore.service.customDesign;

import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.entity.User;
import com.iharshashkou.internetstore.entity.customDesignEntity.*;
import com.iharshashkou.internetstore.exception.CustomDesignValidationException;
import com.iharshashkou.internetstore.repository.CustomDesignRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomDesignServiceSubmitTest {

    @Mock
    private CustomDesignRepository customDesignRepository;

    @Mock
    private Category category;

    @InjectMocks
    private CustomDesignService customDesignService;

    @Test
    void submitDesign_shouldSubmitGuestDesign() {
        String token = "guest-token";
        String tokenHash = hashToken(token);

        CustomDesign design = CustomDesign.builder()
                .id(5L)
                .category(category)
                .woodType(WoodType.OAK)
                .shape(DesignShape.RECTANGULAR)
                .epoxyEnabled(true)
                .epoxyColor(EpoxyColor.BLUE)
                .background(DesignBackground.INTERIOR)
                .estimatedPrice(new BigDecimal("25000.00"))
                .status(CustomDesignStatus.GENERATED)
                .guestTokenHash(tokenHash)
                .build();

        when(category.getId()).thenReturn(1L);
        when(category.getName()).thenReturn("Столы");

        when(customDesignRepository.findByIdAndGuestTokenHash(5L, tokenHash))
                .thenReturn(Optional.of(design));

        when(customDesignRepository.save(any(CustomDesign.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = customDesignService.submitDesign(5L, null, token);

        assertEquals(CustomDesignStatus.SUBMITTED, response.status());
        assertEquals(CustomDesignStatus.SUBMITTED, design.getStatus());

        verify(customDesignRepository).save(design);
    }

    @Test
    void submitDesign_shouldSubmitUserDesign() {
        User user = mock(User.class);

        CustomDesign design = CustomDesign.builder()
                .id(5L)
                .user(user)
                .category(category)
                .woodType(WoodType.OAK)
                .shape(DesignShape.RECTANGULAR)
                .epoxyEnabled(true)
                .epoxyColor(EpoxyColor.BLUE)
                .background(DesignBackground.INTERIOR)
                .estimatedPrice(new BigDecimal("25000.00"))
                .status(CustomDesignStatus.GENERATED)
                .build();

        when(category.getId()).thenReturn(1L);
        when(category.getName()).thenReturn("Столы");

        when(customDesignRepository.findByIdAndUser_Id(5L, 10L))
                .thenReturn(Optional.of(design));

        when(customDesignRepository.save(any(CustomDesign.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = customDesignService.submitDesign(5L, 10L, null);

        assertEquals(CustomDesignStatus.SUBMITTED, response.status());
        assertEquals(CustomDesignStatus.SUBMITTED, design.getStatus());

        verify(customDesignRepository).save(design);
    }

    @Test
    void submitDesign_shouldThrowExceptionWhenDesignAlreadySubmitted() {
        String token = "guest-token";
        String tokenHash = hashToken(token);

        CustomDesign design = CustomDesign.builder()
                .id(5L)
                .category(category)
                .status(CustomDesignStatus.SUBMITTED)
                .build();

        when(customDesignRepository.findByIdAndGuestTokenHash(5L, tokenHash))
                .thenReturn(Optional.of(design));

        assertThrows(
                CustomDesignValidationException.class,
                () -> customDesignService.submitDesign(5L, null, token)
        );

        verify(customDesignRepository, never()).save(any());
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
}
