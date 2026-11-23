package com.iharshashkou.internetstore.service.product;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.entity.Product;
import com.iharshashkou.internetstore.DTO.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceFindByCategoryTest extends BaseProductServiceTest {

    @Test
    void findByCategory_shouldReturnProducts_whenCategoryIsActive() {
        Long categoryId = 1L;

        Category category = Category.builder()
                .id(categoryId)
                .name("Tables")
                .active(true)
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Oak Table")
                .description("Solid oak table")
                .price(new BigDecimal("500.00"))
                .stockQuantity(5)
                .imageUrl("image.jpg")
                .active(true)
                .category(category)
                .build();

        PageRequest pageable = PageRequest.of(0, 10);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(productRepository.findByActiveTrueAndCategory_Id(categoryId, pageable))
                .thenReturn(new PageImpl<>(List.of(product)));

        Page<ProductResponse> result = productService.findByCategory(categoryId, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Oak Table", result.getContent().getFirst().name());
        assertEquals("Tables", result.getContent().getFirst().category().name());

        verify(categoryRepository).findById(categoryId);
        verify(productRepository).findByActiveTrueAndCategory_Id(categoryId, pageable);
    }

    @Test
    void findByCategory_shouldThrowException_whenCategoryIsInactive() {
        Long categoryId = 1L;

        Category category = Category.builder()
                .id(categoryId)
                .name("Tables")
                .active(false)
                .build();

        PageRequest pageable = PageRequest.of(0, 10);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> productService.findByCategory(categoryId, pageable)
        );

        assertEquals("Active category not found: " + categoryId, exception.getMessage());

        verify(categoryRepository).findById(categoryId);
        verifyNoInteractions(productRepository);
    }

    @Test
    void findByCategory_shouldThrowException_whenCategoryDoesNotExist() {
        Long categoryId = 99L;
        PageRequest pageable = PageRequest.of(0, 10);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> productService.findByCategory(categoryId, pageable)
        );

        assertEquals("Active category not found: " + categoryId, exception.getMessage());

        verify(categoryRepository).findById(categoryId);
        verifyNoInteractions(productRepository);
    }
}
