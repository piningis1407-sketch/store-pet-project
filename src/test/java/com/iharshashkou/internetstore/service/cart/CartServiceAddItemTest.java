package com.iharshashkou.internetstore.service.cart;

import com.iharshashkou.internetstore.entity.Cart;
import com.iharshashkou.internetstore.entity.CartItem;
import com.iharshashkou.internetstore.entity.Product;
import com.iharshashkou.internetstore.exception.CartConflictException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartServiceAddItemTest extends BaseCartServiceTest {

    @Test
    void addItem_shouldAddNewProductToExistingCart() {
        Product product = Product.builder()
                .id(1L)
                .name("Стол из дуба")
                .price(new BigDecimal("100.00"))
                .stockQuantity(5)
                .imageUrl("table.jpg")
                .active(true)
                .build();

        Cart cart = Cart.builder()
                .id(1L)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart_IdAndProduct_Id(1L, 1L))
                .thenReturn(Optional.empty());

        when(cartItemRepository.findAllByCart_Id(1L))
                .thenReturn(List.of(
                        CartItem.builder()
                                .cart(cart)
                                .product(product)
                                .quantity(2)
                                .build()
                ));

        var result = cartService.addItem(1L, 1L, 2);

        assertEquals(1, result.items().size());
        assertEquals(2, result.totalItems());
        assertEquals(new BigDecimal("200.00"), result.totalPrice());

        verify(cartItemRepository).save(argThat(item ->
                item.getQuantity() == 2
                        && item.getProduct().getId().equals(1L)
        ));
    }

    @Test
    void addItem_shouldCreateCartWhenCartDoesNotExist() {
        Product product = Product.builder()
                .id(1L)
                .name("Стол из дуба")
                .price(new BigDecimal("100.00"))
                .stockQuantity(5)
                .active(true)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(
                        Cart.builder().id(1L).build()
                ));

        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> {
                    Cart cart = invocation.getArgument(0);
                    cart.setId(1L);
                    return cart;
                });

        when(cartItemRepository.findByCart_IdAndProduct_Id(1L, 1L))
                .thenReturn(Optional.empty());

        when(cartItemRepository.findAllByCart_Id(1L))
                .thenReturn(List.of());

        cartService.addItem(1L, 1L, 2);

        verify(cartRepository).save(argThat(cart ->
                cart.getUser().getId().equals(1L)
        ));

        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void addItem_shouldIncreaseQuantityWhenProductAlreadyExists() {
        Product product = Product.builder()
                .id(1L)
                .name("Стол из дуба")
                .price(new BigDecimal("100.00"))
                .stockQuantity(10)
                .active(true)
                .build();

        Cart cart = Cart.builder()
                .id(1L)
                .build();

        CartItem existingItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart_IdAndProduct_Id(1L, 1L))
                .thenReturn(Optional.of(existingItem));

        when(cartItemRepository.findAllByCart_Id(1L))
                .thenReturn(List.of(existingItem));

        var result = cartService.addItem(1L, 1L, 3);

        assertEquals(5, existingItem.getQuantity());
        assertEquals(5, result.totalItems());
        assertEquals(new BigDecimal("500.00"), result.totalPrice());

        verify(cartItemRepository).save(existingItem);
    }

    @Test
    void addItem_shouldThrowExceptionWhenQuantityIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> cartService.addItem(1L, 1L, null)
        );

        verifyNoInteractions(productRepository, cartRepository,
                cartItemRepository);
    }

    @Test
    void addItem_shouldThrowExceptionWhenQuantityIsZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> cartService.addItem(1L, 1L, 0)
        );

        verifyNoInteractions(productRepository, cartRepository,
                cartItemRepository);
    }

    @Test
    void addItem_shouldThrowExceptionWhenQuantityIsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> cartService.addItem(1L, 1L, -1)
        );

        verifyNoInteractions(productRepository, cartRepository,
                cartItemRepository);
    }

    @Test
    void addItem_shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> cartService.addItem(1L, 1L, 2)
        );

        verify(cartRepository, never()).findByUser_Id(anyLong());
        verifyNoInteractions(cartItemRepository);
    }

    @Test
    void addItem_shouldThrowExceptionWhenProductIsInactive() {
        Product product = Product.builder()
                .id(1L)
                .name("Стол из дуба")
                .stockQuantity(5)
                .active(false)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                CartConflictException.class,
                () -> cartService.addItem(1L, 1L, 2)
        );

        verifyNoInteractions(cartRepository, cartItemRepository);
    }

    @Test
    void addItem_shouldThrowExceptionWhenProductIsOutOfStock() {
        Product product = Product.builder()
                .id(1L)
                .name("Стол из дуба")
                .stockQuantity(0)
                .active(true)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                CartConflictException.class,
                () -> cartService.addItem(1L, 1L, 1)
        );

        verifyNoInteractions(cartRepository, cartItemRepository);
    }

    @Test
    void addItem_shouldThrowExceptionWhenQuantityExceedsStock() {
        Product product = Product.builder()
                .id(1L)
                .name("Стол из дуба")
                .stockQuantity(5)
                .active(true)
                .build();

        Cart cart = Cart.builder()
                .id(1L)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart_IdAndProduct_Id(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                CartConflictException.class,
                () -> cartService.addItem(1L, 1L, 6)
        );

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void addItem_shouldThrowExceptionWhenUpdatedQuantityExceedsStock() {
        Product product = Product.builder()
                .id(1L)
                .name("Стол из дуба")
                .stockQuantity(5)
                .active(true)
                .build();

        Cart cart = Cart.builder()
                .id(1L)
                .build();

        CartItem existingItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(3)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart_IdAndProduct_Id(1L, 1L))
                .thenReturn(Optional.of(existingItem));

        assertThrows(
                CartConflictException.class,
                () -> cartService.addItem(1L, 1L, 3)
        );

        assertEquals(3, existingItem.getQuantity());

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }
}
