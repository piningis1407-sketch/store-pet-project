package com.iharshashkou.internetstore.repository.cart;

import com.iharshashkou.internetstore.DTO.user.Role;
import com.iharshashkou.internetstore.entity.Cart;
import com.iharshashkou.internetstore.entity.User;
import com.iharshashkou.internetstore.repository.BaseRepositoryTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartRepositoryTest extends BaseRepositoryTest {

    @Test
    void findByUserId_shouldReturnCartWhenUserExists() {
        User user = userRepository.save(
                User.builder()
                        .name("Cart User")
                        .email("cart@example.com")
                        .password("encodedPassword")
                        .role(Role.USER)
                        .build()
        );

        Cart cart = cartRepository.save(
                Cart.builder()
                        .user(user)
                        .build()
        );

        var result = cartRepository.findByUser_Id(user.getId());

        assertTrue(result.isPresent());
        assertEquals(cart.getId(), result.get().getId());
        assertEquals(user.getId(), result.get().getUser().getId());
    }

    @Test
    void findByUserIdForUpdate_shouldReturnCart() {
        User user = userRepository.save(
                User.builder()
                        .name("Locked Cart User")
                        .email("locked-cart@example.com")
                        .password("encodedPassword")
                        .role(Role.USER)
                        .build()
        );

        Cart cart = cartRepository.save(
                Cart.builder()
                        .user(user)
                        .build()
        );

        var result = cartRepository.findByUserIdForUpdate(user.getId());

        assertTrue(result.isPresent());
        assertEquals(cart.getId(), result.get().getId());
        assertEquals(user.getId(), result.get().getUser().getId());
    }
}
