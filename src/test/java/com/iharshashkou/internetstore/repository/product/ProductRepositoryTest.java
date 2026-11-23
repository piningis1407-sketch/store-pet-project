package com.iharshashkou.internetstore.repository.product;

import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.entity.Product;
import com.iharshashkou.internetstore.repository.BaseProductRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryTest extends BaseProductRepositoryTest {

    @Test
    void findByActiveTrue_shouldReturnOnlyActiveProducts() {
        Product activeProduct1 = createProduct("Table", true);
        Product activeProduct2 = createProduct("Chair", true);
        Product inactiveProduct = createProduct("Shelf", false);

        productRepository.saveAll(
                java.util.List.of(
                        activeProduct1,
                        activeProduct2,
                        inactiveProduct
                )
        );

        Page<Product> result = productRepository.findByActiveTrue(
                PageRequest.of(0, 10)
        );

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().allMatch(Product::isActive));
    }

    private Product createProduct(String name, boolean active) {
        return Product.builder()
                .name(name)
                .description("Test description")
                .price(new BigDecimal("100.00"))
                .stockQuantity(10)
                .active(active)
                .build();
    }

    @Test
    void findByActiveTrueAndCategoryId_shouldReturnActiveProductsFromCategory() {
        Category category1 = categoryRepository.save(
                Category.builder()
                        .name("Tables")
                        .description("Test category")
                        .build()
        );

        Category category2 = categoryRepository.save(
                Category.builder()
                        .name("Chairs")
                        .description("Test category")
                        .build()
        );

        Product product1 = createProduct("Oak table", true);
        product1.setCategory(category1);

        Product product2 = createProduct("Epoxy table", true);
        product2.setCategory(category1);

        Product product3 = createProduct("Oak chair", true);
        product3.setCategory(category2);

        Product product4 = createProduct("Old table", false);
        product4.setCategory(category1);

        productRepository.saveAll(
                java.util.List.of(product1, product2, product3, product4)
        );

        Page<Product> result = productRepository.findByActiveTrueAndCategory_Id(
                category1.getId(),
                PageRequest.of(0, 10)
        );

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(Product::isActive));
        assertTrue(result.getContent().stream()
                .allMatch(product -> product.getCategory().getId().equals(category1.getId())));
    }

    @Test
    void searchActiveProducts_shouldFilterByNameAndCategory() {
        Category tables = categoryRepository.save(
                Category.builder()
                        .name("Search Tables")
                        .description("Test category")
                        .build()
        );

        Category chairs = categoryRepository.save(
                Category.builder()
                        .name("Search Chairs")
                        .description("Test category")
                        .build()
        );

        Product product1 = createProduct("Oak River Table", true);
        product1.setCategory(tables);

        Product product2 = createProduct("Walnut River Table", true);
        product2.setCategory(tables);

        Product product3 = createProduct("Oak Chair", true);
        product3.setCategory(chairs);

        Product product4 = createProduct("Oak River Table Inactive", false);
        product4.setCategory(tables);

        productRepository.saveAll(
                java.util.List.of(product1, product2, product3, product4)
        );

        Page<Product> result = productRepository.searchActiveProducts(
                tables.getId(),
                "river",
                PageRequest.of(0, 10)
        );

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(Product::isActive));
        assertTrue(result.getContent().stream()
                .allMatch(product -> product.getName().toLowerCase().contains("river")));
    }

    @Test
    void existsByCategoryIdAndActiveTrue_shouldReturnCorrectResult() {
        Category category1 = categoryRepository.save(
                Category.builder()
                        .name("Active Products")
                        .description("Test category")
                        .build()
        );

        Category category2 = categoryRepository.save(
                Category.builder()
                        .name("Inactive Products")
                        .description("Test category")
                        .build()
        );

        Product activeProduct = createProduct("Active table", true);
        activeProduct.setCategory(category1);

        Product inactiveProduct = createProduct("Inactive table", false);
        inactiveProduct.setCategory(category2);

        productRepository.saveAll(
                java.util.List.of(activeProduct, inactiveProduct)
        );

        assertTrue(productRepository.existsByCategory_IdAndActiveTrue(category1.getId()));
        assertFalse(productRepository.existsByCategory_IdAndActiveTrue(category2.getId()));
    }

    @Test
    void findByIdForUpdate_shouldReturnProduct() {
        Product product = productRepository.save(
                createProduct("Locked table", true)
        );

        Product result = productRepository.findByIdForUpdate(product.getId())
                .orElseThrow();

        assertEquals(product.getId(), result.getId());
        assertEquals("Locked table", result.getName());
    }
}
