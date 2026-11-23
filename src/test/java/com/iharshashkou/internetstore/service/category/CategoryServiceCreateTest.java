package com.iharshashkou.internetstore.service.category;

import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.entity.CategoryType;
import com.iharshashkou.internetstore.exception.CategoryAlreadyExistsException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryServiceCreateTest extends BaseCategoryServiceTest {

    @Test
    void create_shouldCreateCategorySuccessfully() {
        when(categoryRepository.existsByNameIgnoreCase("Столы"))
                .thenReturn(false);

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> {
                    Category category = invocation.getArgument(0);
                    category.setId(1L);
                    category.setCreatedAt(Instant.now());
                    return category;
                });

        var result = categoryService.create(
                " Столы ",
                "Столы из дерева"
        );

        assertEquals(1L, result.id());
        assertEquals("Столы", result.name());
        assertEquals("Столы из дерева", result.description());
        assertTrue(result.active());

        verify(categoryRepository).existsByNameIgnoreCase("Столы");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void create_shouldSetChairType() {
        when(categoryRepository.existsByNameIgnoreCase("Стулья"))
                .thenReturn(false);

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        categoryService.create("Стулья", "Стулья из дерева");

        verify(categoryRepository).save(argThat(category ->
                category.getType() == CategoryType.CHAIR
        ));
    }

    @Test
    void create_shouldSetOtherType() {
        when(categoryRepository.existsByNameIgnoreCase("Декор"))
                .thenReturn(false);

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        categoryService.create("Декор", "Деревянный декор");

        verify(categoryRepository).save(argThat(category ->
                category.getType() == CategoryType.OTHER
        ));
    }

    @Test
    void create_shouldThrowExceptionWhenCategoryAlreadyExists() {
        when(categoryRepository.existsByNameIgnoreCase("Столы"))
                .thenReturn(true);

        assertThrows(
                CategoryAlreadyExistsException.class,
                () -> categoryService.create(" Столы ", "Описание")
        );

        verify(categoryRepository, never()).save(any(Category.class));
    }
}
