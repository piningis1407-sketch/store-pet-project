package com.iharshashkou.internetstore.service;

import com.iharshashkou.internetstore.DTO.CategoryResponse;
import com.iharshashkou.internetstore.DTO.ProductRequest;
import com.iharshashkou.internetstore.DTO.ProductResponse;
import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.entity.Product;
import com.iharshashkou.internetstore.repository.CategoryRepository;
import com.iharshashkou.internetstore.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findByCategory(
            Long categoryId,
            Pageable pageable
    ) {
        categoryRepository.findById(categoryId)
                .filter(Category::isActive)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Active category not found: " + categoryId
                        )
                );

        return productRepository
                .findByActiveTrueAndCategory_Id(categoryId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .filter(Product::isActive)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Product not found: " + id));

        return toResponse(product);
    }

    public ProductResponse create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .imageUrl(request.imageUrl())
                .category(resolveCategory(request))
                .active(true)
                .build();

        return toResponse(productRepository.save(product));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Product not found: " + id));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setImageUrl(request.imageUrl());
        product.setCategory(resolveCategory(request));

        return toResponse(productRepository.save(product));
    }

    public void deactivate(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Product not found: " + id));

        product.setActive(false);
        productRepository.save(product);
    }

    private Category resolveCategory(ProductRequest request) {
        if (request.categoryId() == null) {
            return null;
        }

        return categoryRepository.findById(request.categoryId())
                .filter(Category::isActive)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Active category not found: "
                                        + request.categoryId()));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getImageUrl(),
                product.isActive(),
                product.getCreatedAt(),
                toCategoryResponse(product.getCategory())
        );
    }

    private CategoryResponse toCategoryResponse(Category category) {
        if (category == null) {
            return null;
        }

        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(
            Long categoryId,
            String search,
            Pageable pageable
    ) {
        if (categoryId != null) {
            categoryRepository.findById(categoryId)
                    .filter(Category::isActive)
                    .orElseThrow(() ->
                            new EntityNotFoundException(
                                    "Active category not found: " + categoryId
                            )
                    );
        }

        String normalizedSearch = search == null
                ? null
                : search.trim();

        Page<Product> products;

        if (normalizedSearch != null && !normalizedSearch.isEmpty()) {
            products = productRepository.searchActiveProducts(
                    categoryId,
                    normalizedSearch,
                    pageable
            );
        } else if (categoryId != null) {
            products = productRepository.findByActiveTrueAndCategory_Id(
                    categoryId,
                    pageable
            );
        } else {
            products = productRepository.findByActiveTrue(pageable);
        }

        return products.map(this::toResponse);
    }
}