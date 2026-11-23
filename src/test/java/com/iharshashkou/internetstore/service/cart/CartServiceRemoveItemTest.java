package com.iharshashkou.internetstore.service.cart;

import com.iharshashkou.internetstore.entity.Cart;
import com.iharshashkou.internetstore.entity.CartItem;
import com.iharshashkou.internetstore.entity.Product;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceRemoveItemTest extends BaseCartServiceTest {

    @Test
    void removeItem_shouldRemoveProductSuccessfully() {
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
                .thenReturn(List.of());

        var result = cartService.removeItem(1L, 1L);

        assertTrue(result.items().isEmpty());
        assertEquals(0, result.totalItems());
        assertEquals(BigDecimal.ZERO, result.totalPrice());

        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    void removeItem_shouldThrowExceptionWhenCartNotFound() {
        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> cartService.removeItem(1L, 1L)
        );

        verifyNoInteractions(cartItemRepository);
    }

    @Test
    void removeItem_shouldThrowExceptionWhenProductNotInCart() {
        Cart cart = Cart.builder()
                .id(1L)
                .build();

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCart_IdAndProduct_Id(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> cartService.removeItem(1L, 1L)
        );

        verify(cartItemRepository, never()).delete(any(CartItem.class));
    }
}