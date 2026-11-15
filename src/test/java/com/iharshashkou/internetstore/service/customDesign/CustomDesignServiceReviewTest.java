package com.iharshashkou.internetstore.service.customDesign;

import com.iharshashkou.internetstore.DTO.customDesign.ReviewCustomDesignRequest;
import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.entity.customDesignEntity.CustomDesign;
import com.iharshashkou.internetstore.entity.customDesignEntity.CustomDesignStatus;
import com.iharshashkou.internetstore.exception.CustomDesignValidationException;
import com.iharshashkou.internetstore.repository.CategoryRepository;
import com.iharshashkou.internetstore.repository.CustomDesignRepository;
import com.iharshashkou.internetstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomDesignServiceReviewTest {

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

    private CustomDesign createSubmittedDesign() {
        return CustomDesign.builder()
                .id(5L)
                .category(category)
                .estimatedPrice(new BigDecimal("25000.00"))
                .status(CustomDesignStatus.SUBMITTED)
                .build();
    }

    @Test
    void reviewDesign_shouldApproveSubmittedDesign() {
        CustomDesign design = createSubmittedDesign();

        ReviewCustomDesignRequest request = new ReviewCustomDesignRequest(
                CustomDesignStatus.APPROVED,
                new BigDecimal("30000.00"),
                "Проект одобрен"
        );

        when(customDesignRepository.findById(5L))
                .thenReturn(Optional.of(design));

        when(customDesignRepository.save(any(CustomDesign.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        stubCategoryDetails();
        var response = customDesignService.reviewDesign(5L, request);

        assertEquals(CustomDesignStatus.APPROVED, response.status());
        assertEquals(new BigDecimal("30000.00"), response.finalPrice());
        assertEquals("Проект одобрен", response.adminComment());

        verify(customDesignRepository).save(design);
    }

    @Test
    void reviewDesign_shouldRejectSubmittedDesign() {
        CustomDesign design = createSubmittedDesign();

        ReviewCustomDesignRequest request = new ReviewCustomDesignRequest(
                CustomDesignStatus.REJECTED,
                null,
                "Проект требует доработки"
        );

        when(customDesignRepository.findById(5L))
                .thenReturn(Optional.of(design));

        when(customDesignRepository.save(any(CustomDesign.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        stubCategoryDetails();
        var response = customDesignService.reviewDesign(5L, request);

        assertEquals(CustomDesignStatus.REJECTED, response.status());
        assertNull(response.finalPrice());
        assertEquals("Проект требует доработки", response.adminComment());

        verify(customDesignRepository).save(design);
    }

    @Test
    void reviewDesign_shouldThrowExceptionWhenApprovalHasNoFinalPrice() {
        CustomDesign design = createSubmittedDesign();

        ReviewCustomDesignRequest request = new ReviewCustomDesignRequest(
                CustomDesignStatus.APPROVED,
                null,
                null
        );

        when(customDesignRepository.findById(5L))
                .thenReturn(Optional.of(design));

        assertThrows(
                CustomDesignValidationException.class,
                () -> customDesignService.reviewDesign(5L, request)
        );

        verify(customDesignRepository, never()).save(any());
    }

    @Test
    void reviewDesign_shouldThrowExceptionWhenRejectedDesignHasFinalPrice() {
        CustomDesign design = createSubmittedDesign();

        ReviewCustomDesignRequest request = new ReviewCustomDesignRequest(
                CustomDesignStatus.REJECTED,
                new BigDecimal("30000.00"),
                null
        );

        when(customDesignRepository.findById(5L))
                .thenReturn(Optional.of(design));

        assertThrows(
                CustomDesignValidationException.class,
                () -> customDesignService.reviewDesign(5L, request)
        );

        verify(customDesignRepository, never()).save(any());
    }

    @Test
    void reviewDesign_shouldThrowExceptionWhenStatusIsInvalid() {
        CustomDesign design = createSubmittedDesign();

        ReviewCustomDesignRequest request = new ReviewCustomDesignRequest(
                CustomDesignStatus.GENERATED,
                null,
                null
        );

        when(customDesignRepository.findById(5L))
                .thenReturn(Optional.of(design));

        assertThrows(
                CustomDesignValidationException.class,
                () -> customDesignService.reviewDesign(5L, request)
        );

        verify(customDesignRepository, never()).save(any());
    }

    @Test
    void reviewDesign_shouldThrowExceptionWhenDesignIsNotSubmitted() {
        CustomDesign design = CustomDesign.builder()
                .id(5L)
                .category(category)
                .status(CustomDesignStatus.GENERATED)
                .build();

        ReviewCustomDesignRequest request = new ReviewCustomDesignRequest(
                CustomDesignStatus.APPROVED,
                new BigDecimal("30000.00"),
                null
        );

        when(customDesignRepository.findById(5L))
                .thenReturn(Optional.of(design));

        assertThrows(
                CustomDesignValidationException.class,
                () -> customDesignService.reviewDesign(5L, request)
        );

        verify(customDesignRepository, never()).save(any());
    }

    private void stubCategoryDetails() {
        when(category.getId()).thenReturn(1L);
        when(category.getName()).thenReturn("Столы");
    }
}