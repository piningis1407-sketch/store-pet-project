package com.iharshashkou.internetstore.controller;

import com.iharshashkou.internetstore.DTO.AddCartItemRequest;
import com.iharshashkou.internetstore.DTO.CartResponse;
import com.iharshashkou.internetstore.DTO.UpdateCartItemRequest;
import com.iharshashkou.internetstore.repository.UserRepository;
import com.iharshashkou.internetstore.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);

        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @Valid @RequestBody AddCartItemRequest request,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);

        return ResponseEntity.ok(
                cartService.addItem(
                        userId,
                        request.productId(),
                        request.quantity()
                )
        );
    }

    @PatchMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateItem(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);

        return ResponseEntity.ok(
                cartService.updateItem(
                        userId,
                        productId,
                        request.quantity()
                )
        );
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable Long productId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);

        return ResponseEntity.ok(
                cartService.removeItem(userId, productId)
        );
    }

    @DeleteMapping
    public ResponseEntity<CartResponse> clearCart(
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);

        return ResponseEntity.ok(cartService.clearCart(userId));
    }

    private Long getUserId(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Пользователь не найден"
                ))
                .getId();
    }
}
