package com.iharshashkou.internetstore.service.product;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import com.iharshashkou.internetstore.DTO.product.ProductRequest;
import com.iharshashkou.internetstore.DTO.product.ProductResponse;
import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.entity.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceCreateTest extends BaseProductServiceTest {

    @Test
    void create_shouldSaveProduct_whenCategoryIsActive() {
        Long categoryId = 1L;

        ProductRequest request = new ProductRequest(
                "Oak Table",
                "Solid oak table",
                new BigDecimal("500.00"),
                5,
                "image.jpg",
                categoryId
        );

        Category category = Category.builder()
                .id(categoryId)
                .name("Tables")
                .active(true)
                .build();

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    product.setId(1L);
                    product.setCreatedAt(Instant.now());
                    return product;
                });

        ProductResponse result = productService.create(request);

        assertEquals(1L, result.id());
        assertEquals("Oak Table", result.name());
        assertEquals("Solid oak table", result.description());
        assertEquals(new BigDecimal("500.00"), result.price());
        assertEquals(5, result.stockQuantity());
        assertEquals("image.jpg", result.imageUrl());
        assertTrue(result.active());
        assertEquals("Tables", result.category().name());

        verify(categoryRepository).findById(categoryId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void create_shouldSaveProduct_withoutCategory() {
        ProductRequest request = new ProductRequest(
                "Oak Table",
                "Solid oak table",
                new BigDecimal("500.00"),
                5,
                "image.jpg",
                null
        );

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    product.setId(1L);
                    product.setCreatedAt(Instant.now());
                    return product;
                });

        ProductResponse result = productService.create(request);

        assertEquals("Oak Table", result.name());
        assertTrue(result.active());
        assertNull(result.category());

        verify(productRepository).save(any(Product.class));
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void create_shouldThrowException_whenCategoryIsInactive() {
        Long categoryId = 1L;

        ProductRequest request = new ProductRequest(
                "Oak Table",
                "Solid oak table",
                new BigDecimal("500.00"),
                5,
                "image.jpg",
                categoryId
        );

        Category category = Category.builder()
                .id(categoryId)
                .name("Tables")
                .active(false)
                .build();

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> productService.create(request)
        );

        assertEquals("Active category not found: " + categoryId, exception.getMessage());

        verify(categoryRepository).findById(categoryId);
        verifyNoInteractions(productRepository);
    }

    @Test
    void create_shouldThrowException_whenCategoryDoesNotExist() {
        Long categoryId = 99L;

        ProductRequest request = new ProductRequest(
                "Oak Table",
                "Solid oak table",
                new BigDecimal("500.00"),
                5,
                "image.jpg",
                categoryId
        );

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> productService.create(request)
        );

        assertEquals("Active category not found: " + categoryId, exception.getMessage());

        verify(categoryRepository).findById(categoryId);
        verifyNoInteractions(productRepository);
    }
}
