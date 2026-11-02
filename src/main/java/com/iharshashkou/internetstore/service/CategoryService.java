package com.iharshashkou.internetstore.service;

import com.iharshashkou.internetstore.DTO.CategoryResponse;
import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.exception.CategoryAlreadyExistsException;
import com.iharshashkou.internetstore.exception.CategoryHasActiveProductsException;
import com.iharshashkou.internetstore.repository.CategoryRepository;
import com.iharshashkou.internetstore.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse create(String name, String description) {
        if (categoryRepository.existsByNameIgnoreCase(name.trim())) {
            throw new CategoryAlreadyExistsException(
                    "Category already exists: " + name.trim()
            );
        }

        Category category = Category.builder()
                .name(name.trim())
                .description(description)
                .active(true)
                .build();

        return toResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id)
                .filter(Category::isActive)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Category not found: " + id));

        return toResponse(category);
    }

    public void deactivate(Long id) {
        Category category = categoryRepository.findById(id)
                .filter(Category::isActive)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Category not found: " + id));

        if (productRepository.existsByCategory_IdAndActiveTrue(id)) {
            throw new CategoryHasActiveProductsException(id);
        }

        category.setActive(false);
        categoryRepository.save(category);
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }
}