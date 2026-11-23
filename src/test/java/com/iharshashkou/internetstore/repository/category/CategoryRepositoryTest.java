package com.iharshashkou.internetstore.repository.category;

import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.repository.BaseProductRepositoryTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryRepositoryTest extends BaseProductRepositoryTest {

    @Test
    void findByActiveTrueOrderByNameAsc_shouldReturnOnlyActiveCategoriesSortedByName() {
        Category tables = createCategory("Tables", true);
        Category chairs = createCategory("Chairs", true);
        Category shelves = createCategory("Shelves", false);

        categoryRepository.saveAll(List.of(tables, chairs, shelves));

        List<Category> result = categoryRepository.findByActiveTrueOrderByNameAsc();

        assertEquals(2, result.size());
        assertEquals("Chairs", result.get(0).getName());
        assertEquals("Tables", result.get(1).getName());
        assertTrue(result.stream().allMatch(Category::isActive));
    }

    private Category createCategory(String name, boolean active) {
        return Category.builder()
                .name(name)
                .description("Test category")
                .active(active)
                .build();
    }

    @Test
    void existsByNameIgnoreCase_shouldIgnoreCase() {
        categoryRepository.save(createCategory("Oak Tables", true));

        assertTrue(categoryRepository.existsByNameIgnoreCase("oak tables"));
        assertTrue(categoryRepository.existsByNameIgnoreCase("OAK TABLES"));
        assertFalse(categoryRepository.existsByNameIgnoreCase("Oak Chairs"));
    }

    @Test
    void findAllByOrderByNameAsc_shouldReturnAllCategoriesSortedByName() {
        categoryRepository.saveAll(List.of(
                createCategory("Tables", true),
                createCategory("Chairs", true),
                createCategory("Shelves", false)
        ));

        List<Category> result = categoryRepository.findAllByOrderByNameAsc();

        assertEquals(3, result.size());
        assertEquals("Chairs", result.get(0).getName());
        assertEquals("Shelves", result.get(1).getName());
        assertEquals("Tables", result.get(2).getName());
    }

    @Test
    void existsByNameIgnoreCaseAndIdNot_shouldExcludeCategoryWithGivenId() {
        Category tables = categoryRepository.save(
                createCategory("Oak Tables", true)
        );

        Category chairs = categoryRepository.save(
                createCategory("Oak Chairs", true)
        );

        assertFalse(
                categoryRepository.existsByNameIgnoreCaseAndIdNot(
                        "Oak Tables",
                        tables.getId()
                )
        );

        assertTrue(
                categoryRepository.existsByNameIgnoreCaseAndIdNot(
                        "oak tables",
                        chairs.getId()
                )
        );

        assertFalse(
                categoryRepository.existsByNameIgnoreCaseAndIdNot(
                        "Pine Tables",
                        chairs.getId()
                )
        );
    }
}
