package com.iharshashkou.internetstore.service.category;

import com.iharshashkou.internetstore.entity.Category;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceActivateTest extends BaseCategoryServiceTest {

    @Test
    void activate_shouldActivateCategorySuccessfully() {
        Category category = Category.builder()
                .id(1L)
                .name("Столы")
                .active(false)
                .build();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        categoryService.activate(1L);

        assertTrue(category.isActive());

        verify(categoryRepository).save(category);
    }

    @Test
    void activate_shouldDoNothingWhenCategoryAlreadyActive() {
        Category category = Category.builder()
                .id(1L)
                .name("Столы")
                .active(true)
                .build();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        categoryService.activate(1L);

        assertTrue(category.isActive());

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void activate_shouldThrowExceptionWhenCategoryNotFound() {
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.activate(1L)
        );

        verify(categoryRepository, never()).save(any(Category.class));
    }
}
