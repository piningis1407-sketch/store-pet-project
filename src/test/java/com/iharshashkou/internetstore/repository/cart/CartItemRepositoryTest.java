package com.iharshashkou.internetstore.repository.cart;

import com.iharshashkou.internetstore.DTO.user.Role;
import com.iharshashkou.internetstore.entity.Cart;
import com.iharshashkou.internetstore.entity.CartItem;
import com.iharshashkou.internetstore.entity.Product;
import com.iharshashkou.internetstore.entity.User;
import com.iharshashkou.internetstore.repository.BaseRepositoryTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartItemRepositoryTest extends BaseRepositoryTest {

    @Test
    void findAllByCartId_shouldReturnAllCartItems() {
        User user = userRepository.save(
                User.builder()
                        .name("Cart Items User")
                        .email("cart-items@example.com")
                        .password("encodedPassword")
                        .role(Role.USER)
                        .build()
        );

        Cart cart = cartRepository.save(
                Cart.builder()
                        .user(user)
                        .build()
        );

        Product product1 = productRepository.save(createProduct("Oak Table"));
        Product product2 = productRepository.save(createProduct("Epoxy Table"));

        cartItemRepository.saveAll(List.of(
                CartItem.builder()
                        .cart(cart)
                        .product(product1)
                        .quantity(2)
                        .build(),
                CartItem.builder()
                        .cart(cart)
                        .product(product2)
                        .quantity(1)
                        .build()
        ));

        List<CartItem> result = cartItemRepository.findAllByCart_Id(cart.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream()
                .allMatch(item -> item.getCart().getId().equals(cart.getId())));
    }

    @Test
    void findByCartIdAndProductId_shouldReturnCartItem() {
        User user = userRepository.save(
                User.builder()
                        .name("Find Cart Item User")
                        .email("find-cart-item@example.com")
                        .password("encodedPassword")
                        .role(Role.USER)
                        .build()
        );

        Cart cart = cartRepository.save(
                Cart.builder()
                        .user(user)
                        .build()
        );

        Product product = productRepository.save(createProduct("Walnut Table"));

        CartItem cartItem = cartItemRepository.save(
                CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(3)
                        .build()
        );

        var result = cartItemRepository.findByCart_IdAndProduct_Id(
                cart.getId(),
                product.getId()
        );

        assertTrue(result.isPresent());
        assertEquals(cartItem.getId(), result.get().getId());
        assertEquals(3, result.get().getQuantity());
    }

    @Test
    void deleteAllByCartId_shouldDeleteOnlyItemsFromSpecifiedCart() {
        User user1 = userRepository.save(
                createUser("Delete User 1", "delete1@example.com")
        );

        User user2 = userRepository.save(
                createUser("Delete User 2", "delete2@example.com")
        );

        Cart cart1 = cartRepository.save(
                Cart.builder().user(user1).build()
        );

        Cart cart2 = cartRepository.save(
                Cart.builder().user(user2).build()
        );

        Product product1 = productRepository.save(createProduct("Table 1"));
        Product product2 = productRepository.save(createProduct("Table 2"));

        cartItemRepository.saveAll(List.of(
                CartItem.builder().cart(cart1).product(product1).quantity(1).build(),
                CartItem.builder().cart(cart1).product(product2).quantity(2).build(),
                CartItem.builder().cart(cart2).product(product1).quantity(3).build()
        ));

        cartItemRepository.deleteAllByCart_Id(cart1.getId());

        assertTrue(cartItemRepository.findAllByCart_Id(cart1.getId()).isEmpty());
        assertEquals(1, cartItemRepository.findAllByCart_Id(cart2.getId()).size());
    }

    private Product createProduct(String name) {
        return Product.builder()
                .name(name)
                .description("Test description")
                .price(new BigDecimal("100.00"))
                .stockQuantity(10)
                .build();
    }

    private User createUser(String name, String email) {
        return User.builder()
                .name(name)
                .email(email)
                .password("encodedPassword")
                .role(Role.USER)
                .build();
    }
}
