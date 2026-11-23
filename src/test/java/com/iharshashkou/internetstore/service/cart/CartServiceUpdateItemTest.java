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
import static org.mockito.Mockito.*;

class CartServiceUpdateItemTest extends BaseCartServiceTest {

    @Test
    void updateItem_shouldUpdateQuantitySuccessfully() {
        Product product = Product.builder()
                .id(1L)
                .name("Стол из дуба")
                .price(new BigDecimal("100.00"))
                .stockQuantity(10)
                .imageUrl("table.jpg")
                .active(true)
                .build();

        Cart cart = Cart.builder()
                .id(1L)
                .build();

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart_IdAndProduct_Id(1L, 1L))
                .thenReturn(Optional.of(cartItem));

        when(cartItemRepository.findAllByCart_Id(1L))
                .thenReturn(List.of(cartItem));

        var result = cartService.updateItem(1L, 1L, 5);

        assertEquals(5, cartItem.getQuantity());
        assertEquals(5, result.totalItems());
        assertEquals(new BigDecimal("500.00"), result.totalPrice());

        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void updateItem_shouldThrowExceptionWhenQuantityIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> cartService.updateItem(1L, 1L, null)
        );

        verifyNoInteractions(cartRepository, cartItemRepository);
    }

    @Test
    void updateItem_shouldThrowExceptionWhenQuantityIsZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> cartService.updateItem(1L, 1L, 0)
        );

        verifyNoInteractions(cartRepository, cartItemRepository);
    }

    @Test
    void updateItem_shouldThrowExceptionWhenQuantityIsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> cartService.updateItem(1L, 1L, -1)
        );

        verifyNoInteractions(cartRepository, cartItemRepository);
    }

    @Test
    void updateItem_shouldThrowExceptionWhenCartNotFound() {
        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> cartService.updateItem(1L, 1L, 3)
        );

        verifyNoInteractions(cartItemRepository);
    }

    @Test
    void updateItem_shouldThrowExceptionWhenProductNotInCart() {
        Cart cart = Cart.builder()
                .id(1L)
                .build();

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart_IdAndProduct_Id(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> cartService.updateItem(1L, 1L, 3)
        );

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void updateItem_shouldThrowExceptionWhenProductIsInactive() {
        Product product = Product.builder()
                .id(1L)
                .stockQuantity(10)
                .active(false)
                .build();

        Cart cart = Cart.builder()
                .id(1L)
                .build();

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart_IdAndProduct_Id(1L, 1L))
                .thenReturn(Optional.of(cartItem));

        assertThrows(
                CartConflictException.class,
                () -> cartService.updateItem(1L, 1L, 3)
        );

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void updateItem_shouldThrowExceptionWhenQuantityExceedsStock() {
        Product product = Product.builder()
                .id(1L)
                .stockQuantity(5)
                .active(true)
                .build();

        Cart cart = Cart.builder()
                .id(1L)
                .build();

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart_IdAndProduct_Id(1L, 1L))
                .thenReturn(Optional.of(cartItem));

        assertThrows(
                CartConflictException.class,
                () -> cartService.updateItem(1L, 1L, 6)
        );

        assertEquals(2, cartItem.getQuantity());
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }
}
