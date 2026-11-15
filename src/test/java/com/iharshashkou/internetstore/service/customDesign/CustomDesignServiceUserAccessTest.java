package com.iharshashkou.internetstore.service.customDesign;

import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.entity.User;
import com.iharshashkou.internetstore.entity.customDesignEntity.CustomDesign;
import com.iharshashkou.internetstore.entity.customDesignEntity.CustomDesignStatus;
import com.iharshashkou.internetstore.entity.customDesignEntity.DesignBackground;
import com.iharshashkou.internetstore.entity.customDesignEntity.DesignShape;
import com.iharshashkou.internetstore.entity.customDesignEntity.EpoxyColor;
import com.iharshashkou.internetstore.entity.customDesignEntity.WoodType;
import com.iharshashkou.internetstore.repository.CustomDesignRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomDesignServiceUserAccessTest {

    @Mock
    private CustomDesignRepository customDesignRepository;

    @Mock
    private Category category;

    @InjectMocks
    private CustomDesignService customDesignService;

    @Test
    void getUserDesign_shouldReturnOwnDesign() {
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

        var response = customDesignService.getUserDesign(10L, 5L);

        assertNotNull(response);
        assertEquals(5L, response.id());
        assertEquals(1L, response.categoryId());
        assertEquals("Столы", response.categoryName());

        verify(customDesignRepository).findByIdAndUser_Id(5L, 10L);
    }

    @Test
    void getUserDesign_shouldThrowExceptionWhenDesignDoesNotBelongToUser() {
        when(customDesignRepository.findByIdAndUser_Id(5L, 10L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> customDesignService.getUserDesign(10L, 5L)
        );

        verify(customDesignRepository).findByIdAndUser_Id(5L, 10L);
    }
}
