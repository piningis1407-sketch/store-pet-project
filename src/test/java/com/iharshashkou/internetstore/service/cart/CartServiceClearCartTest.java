package com.iharshashkou.internetstore.service.cart;

import com.iharshashkou.internetstore.entity.Cart;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceClearCartTest extends BaseCartServiceTest {

    @Test
    void clearCart_shouldClearCartSuccessfully() {
        Cart cart = Cart.builder()
                .id(1L)
                .build();

        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findAllByCart_Id(1L))
                .thenReturn(List.of());

        var result = cartService.clearCart(1L);

        assertTrue(result.items().isEmpty());
        assertEquals(0, result.totalItems());
        assertEquals(BigDecimal.ZERO, result.totalPrice());

        verify(cartItemRepository).deleteAllByCart_Id(1L);
    }

    @Test
    void clearCart_shouldThrowExceptionWhenCartNotFound() {
        when(cartRepository.findByUser_Id(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> cartService.clearCart(1L)
        );

        verify(cartItemRepository, never()).deleteAllByCart_Id(anyLong());
    }
}