package com.iharshashkou.internetstore.service.customDesign;

import com.iharshashkou.internetstore.DTO.customDesign.CreateCustomDesignRequest;
import com.iharshashkou.internetstore.DTO.customDesign.CreateCustomDesignResponse;
import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.entity.User;
import com.iharshashkou.internetstore.entity.customDesignEntity.CustomDesign;
import com.iharshashkou.internetstore.entity.customDesignEntity.CustomDesignStatus;
import com.iharshashkou.internetstore.entity.customDesignEntity.DesignBackground;
import com.iharshashkou.internetstore.entity.customDesignEntity.DesignShape;
import com.iharshashkou.internetstore.entity.customDesignEntity.EpoxyColor;
import com.iharshashkou.internetstore.entity.customDesignEntity.WoodType;
import com.iharshashkou.internetstore.repository.CategoryRepository;
import com.iharshashkou.internetstore.repository.CustomDesignRepository;
import com.iharshashkou.internetstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class CustomDesignServiceCreateTest {

    @Mock
    private CustomDesignRepository customDesignRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomDesignPricingService pricingService;

    @Mock
    private CustomDesignValidationService validationService;

    @Mock
    private Category category;

    @InjectMocks
    private CustomDesignService customDesignService;

    private CreateCustomDesignRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateCustomDesignRequest(
                1L,
                WoodType.OAK,
                DesignShape.RECTANGULAR,
                true,
                EpoxyColor.BLUE,
                DesignBackground.INTERIOR,
                120,
                80,
                75,
                null,
                "Тестовый проект"
        );
    }

    private void stubCreateDesignDependencies() {
        when(category.getId()).thenReturn(1L);
        when(category.getName()).thenReturn("Столы");
        when(category.isActive()).thenReturn(true);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(pricingService.calculatePrice(request))
                .thenReturn(new BigDecimal("25000.00"));

        when(customDesignRepository.save(any(CustomDesign.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createDesign_shouldCreateGuestDesignAndReturnAccessToken() {
        stubCreateDesignDependencies();

        CreateCustomDesignResponse response =
                customDesignService.createDesign(null, request);

        assertNotNull(response);
        assertNotNull(response.design());
        assertNotNull(response.guestAccessToken());
        assertFalse(response.guestAccessToken().isBlank());

        assertEquals(1L, response.design().categoryId());
        assertEquals("Столы", response.design().categoryName());
        assertEquals(CustomDesignStatus.GENERATED, response.design().status());
        assertEquals(new BigDecimal("25000.00"),
                response.design().estimatedPrice());

        verify(validationService).validateDimensions(request);
        verify(categoryRepository).findById(1L);
        verifyNoInteractions(userRepository);

        ArgumentCaptor<CustomDesign> captor =
                ArgumentCaptor.forClass(CustomDesign.class);

        verify(customDesignRepository).save(captor.capture());

        CustomDesign savedDesign = captor.getValue();

        assertNull(savedDesign.getUser());
        assertNotNull(savedDesign.getGuestTokenHash());
        assertNotEquals(response.guestAccessToken(),
                savedDesign.getGuestTokenHash());

        assertEquals(
                hashToken(response.guestAccessToken()),
                savedDesign.getGuestTokenHash()
        );

        assertEquals(CustomDesignStatus.GENERATED, savedDesign.getStatus());
        assertEquals(new BigDecimal("25000.00"),
                savedDesign.getEstimatedPrice());
    }

    @Test
    void createDesign_shouldCreateAuthenticatedUserDesignWithoutGuestToken() {
        stubCreateDesignDependencies();

        User user = mock(User.class);

        when(userRepository.findById(10L))
                .thenReturn(Optional.of(user));

        CreateCustomDesignResponse response =
                customDesignService.createDesign(10L, request);

        assertNotNull(response);
        assertNotNull(response.design());
        assertNull(response.guestAccessToken());

        assertEquals(CustomDesignStatus.GENERATED, response.design().status());
        assertEquals(new BigDecimal("25000.00"),
                response.design().estimatedPrice());

        verify(validationService).validateDimensions(request);
        verify(categoryRepository).findById(1L);
        verify(userRepository).findById(10L);

        verify(customDesignRepository).save(argThat(design ->
                design.getUser() == user
                        && design.getGuestTokenHash() == null
                        && design.getStatus() == CustomDesignStatus.GENERATED
        ));
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
