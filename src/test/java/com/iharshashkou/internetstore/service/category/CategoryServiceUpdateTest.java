package com.iharshashkou.internetstore.service.category;

import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.entity.CategoryType;
import com.iharshashkou.internetstore.exception.CategoryAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryServiceUpdateTest extends BaseCategoryServiceTest {

    @Test
    void update_shouldUpdateCategorySuccessfully() {
        Category category = Category.builder()
                .id(1L)
                .name("Старое название")
                .description("Старое описание")
                .active(true)
                .build();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.existsByNameIgnoreCaseAndIdNot(
                "Стулья", 1L))
                .thenReturn(false);

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = categoryService.update(
                1L,
                " Стулья ",
                "Стулья из дерева"
        );

        assertEquals("Стулья", result.name());
        assertEquals("Стулья из дерева", result.description());
        assertTrue(result.active());
        assertEquals(CategoryType.CHAIR, category.getType());

        verify(categoryRepository)
                .existsByNameIgnoreCaseAndIdNot("Стулья", 1L);
        verify(categoryRepository).save(category);
    }

    @Test
    void update_shouldThrowExceptionWhenCategoryNotFound() {
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.update(1L, "Столы", "Описание")
        );

        verify(categoryRepository, never())
                .save(any(Category.class));
    }

    @Test
    void update_shouldThrowExceptionWhenNameAlreadyExists() {
        Category category = Category.builder()
                .id(1L)
                .name("Старое название")
                .active(true)
                .build();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.existsByNameIgnoreCaseAndIdNot(
                "Столы", 1L))
                .thenReturn(true);

        assertThrows(
                CategoryAlreadyExistsException.class,
                () -> categoryService.update(1L, " Столы ", "Описание")
        );

        verify(categoryRepository, never())
                .save(any(Category.class));
    }
}
