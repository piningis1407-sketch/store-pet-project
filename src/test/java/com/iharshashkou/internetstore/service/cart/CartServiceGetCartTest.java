package com.iharshashkou.internetstore.service.cart;

import com.iharshashkou.internetstore.DTO.cart.CartResponse;
import com.iharshashkou.internetstore.entity.Cart;
import com.iharshashkou.internetstore.entity.CartItem;
import com.iharshashkou.internetstore.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceGetCartTest extends BaseCartServiceTest {

    @Test
    void getCart_shouldReturnEmptyCartWhenCartDoesNotExist() {
        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.empty());

        CartResponse result = cartService.getCart(1L);

        assertNotNull(result);
        assertTrue(result.items().isEmpty());
        assertEquals(0, result.totalItems());
        assertEquals(BigDecimal.ZERO, result.totalPrice());

        verify(cartRepository).findByUser_Id(1L);
        verifyNoInteractions(cartItemRepository);
    }

    @Test
    void getCart_shouldReturnCartWithItemsAndCalculateTotal() {
        Product firstProduct = Product.builder()
                .id(1L)
                .name("Стол из дуба")
                .price(new BigDecimal("100.00"))
                .stockQuantity(5)
                .imageUrl("table.jpg")
                .active(true)
                .build();

        Product secondProduct = Product.builder()
                .id(2L)
                .name("Стул из дуба")
                .price(new BigDecimal("50.00"))
                .stockQuantity(10)
                .imageUrl("chair.jpg")
                .active(true)
                .build();

        Cart cart = Cart.builder()
                .id(1L)
                .build();

        CartItem firstItem = CartItem.builder()
                .cart(cart)
                .product(firstProduct)
                .quantity(2)
                .build();

        CartItem secondItem = CartItem.builder()
                .cart(cart)
                .product(secondProduct)
                .quantity(3)
                .build();

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findAllByCart_Id(1L))
                .thenReturn(java.util.List.of(firstItem, secondItem));

        CartResponse result = cartService.getCart(1L);

        assertEquals(2, result.items().size());
        assertEquals(5, result.totalItems());
        assertEquals(new BigDecimal("350.00"), result.totalPrice());

        assertEquals("Стол из дуба", result.items().getFirst().name());
        assertEquals(2, result.items().getFirst().quantity());
        assertEquals(new BigDecimal("200.00"),
                result.items().getFirst().subtotal());

        assertEquals("Стул из дуба", result.items().get(1).name());
        assertEquals(3, result.items().get(1).quantity());
        assertEquals(new BigDecimal("150.00"),
                result.items().get(1).subtotal());

        verify(cartRepository).findByUser_Id(1L);
        verify(cartItemRepository).findAllByCart_Id(1L);
    }

    @Test
    void getCart_shouldReturnEmptyCartWhenCartHasNoItems() {
        Cart cart = Cart.builder()
                .id(1L)
                .build();

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findAllByCart_Id(1L))
                .thenReturn(java.util.List.of());

        CartResponse result = cartService.getCart(1L);

        assertNotNull(result);
        assertTrue(result.items().isEmpty());
        assertEquals(0, result.totalItems());
        assertEquals(BigDecimal.ZERO, result.totalPrice());

        verify(cartRepository).findByUser_Id(1L);
        verify(cartItemRepository).findAllByCart_Id(1L);
    }
}
