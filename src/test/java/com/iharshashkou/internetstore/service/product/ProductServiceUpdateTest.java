package com.iharshashkou.internetstore.service.product;

import org.junit.jupiter.api.Test;
import com.iharshashkou.internetstore.DTO.product.ProductRequest;
import com.iharshashkou.internetstore.DTO.product.ProductResponse;
import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.entity.Product;
import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceUpdateTest extends BaseProductServiceTest {

    @Test
    void update_shouldUpdateProduct_whenProductExists() {
        Long productId = 1L;
        Long categoryId = 2L;

        Product existingProduct = Product.builder()
                .id(productId)
                .name("Old Table")
                .description("Old description")
                .price(new BigDecimal("300.00"))
                .stockQuantity(2)
                .imageUrl("old-image.jpg")
                .active(false)
                .build();

        Category category = Category.builder()
                .id(categoryId)
                .name("Tables")
                .active(true)
                .build();

        ProductRequest request = new ProductRequest(
                "Oak Table",
                "New description",
                new BigDecimal("500.00"),
                5,
                "new-image.jpg",
                categoryId
        );

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));
        when(productRepository.save(existingProduct))
                .thenReturn(existingProduct);

        ProductResponse result = productService.update(productId, request);

        assertEquals("Oak Table", result.name());
        assertEquals("New description", result.description());
        assertEquals(new BigDecimal("500.00"), result.price());
        assertEquals(5, result.stockQuantity());
        assertEquals("new-image.jpg", result.imageUrl());
        assertFalse(result.active());
        assertEquals("Tables", result.category().name());

        verify(productRepository).findById(productId);
        verify(categoryRepository).findById(categoryId);
        verify(productRepository).save(existingProduct);
    }

    @Test
    void update_shouldThrowException_whenProductDoesNotExist() {
        Long productId = 99L;

        ProductRequest request = new ProductRequest(
                "Oak Table",
                "New description",
                new BigDecimal("500.00"),
                5,
                "image.jpg",
                null
        );

        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> productService.update(productId, request)
        );

        assertEquals("Product not found: " + productId, exception.getMessage());

        verify(productRepository).findById(productId);
        verifyNoInteractions(categoryRepository);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void update_shouldThrowException_whenCategoryIsInactive() {
        Long productId = 1L;
        Long categoryId = 2L;

        Product existingProduct = Product.builder()
                .id(productId)
                .name("Old Table")
                .active(true)
                .build();

        Category category = Category.builder()
                .id(categoryId)
                .name("Tables")
                .active(false)
                .build();

        ProductRequest request = new ProductRequest(
                "Oak Table",
                "New description",
                new BigDecimal("500.00"),
                5,
                "image.jpg",
                categoryId
        );

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> productService.update(productId, request)
        );

        assertEquals("Active category not found: " + categoryId, exception.getMessage());

        verify(productRepository).findById(productId);
        verify(categoryRepository).findById(categoryId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void update_shouldThrowException_whenCategoryDoesNotExist() {
        Long productId = 1L;
        Long categoryId = 99L;

        Product existingProduct = Product.builder()
                .id(productId)
                .name("Old Table")
                .active(true)
                .build();

        ProductRequest request = new ProductRequest(
                "Oak Table",
                "New description",
                new BigDecimal("500.00"),
                5,
                "image.jpg",
                categoryId
        );

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> productService.update(productId, request)
        );

        assertEquals("Active category not found: " + categoryId, exception.getMessage());

        verify(productRepository).findById(productId);
        verify(categoryRepository).findById(categoryId);
        verify(productRepository, never()).save(any(Product.class));
    }
}
