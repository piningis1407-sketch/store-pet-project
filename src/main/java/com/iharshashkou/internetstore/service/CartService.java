package com.iharshashkou.internetstore.service;

import com.iharshashkou.internetstore.DTO.CartItemResponse;
import com.iharshashkou.internetstore.DTO.CartResponse;
import com.iharshashkou.internetstore.entity.Cart;
import com.iharshashkou.internetstore.entity.CartItem;
import com.iharshashkou.internetstore.entity.Product;
import com.iharshashkou.internetstore.entity.User;
import com.iharshashkou.internetstore.exception.CartConflictException;
import com.iharshashkou.internetstore.repository.CartItemRepository;
import com.iharshashkou.internetstore.repository.CartRepository;
import com.iharshashkou.internetstore.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUser_Id(userId)
                .orElse(null);

        if (cart == null) {
            return new CartResponse(List.of(), 0, BigDecimal.ZERO);
        }

        List<CartItemResponse> items = cartItemRepository
                .findAllByCart_Id(cart.getId())
                .stream()
                .map(this::toCartItemResponse)
                .toList();

        int totalItems = items.stream()
                .mapToInt(CartItemResponse::quantity)
                .sum();

        BigDecimal totalPrice = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(items, totalItems, totalPrice);
    }

    public CartResponse addItem(Long userId, Long productId, Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException("Количество должно быть больше нуля");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Товар не найден"));

        if (!product.isActive()) {
            throw new CartConflictException("Товар недоступен");
        }

        if (product.getStockQuantity() < 1) {
            throw new CartConflictException("Товар отсутствует на складе");
        }

        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .user(User.builder().id(userId).build())
                                .build()
                ));

        CartItem cartItem = cartItemRepository
                .findByCart_IdAndProduct_Id(cart.getId(), productId)
                .orElse(null);

        int newQuantity = quantity;

        if (cartItem != null) {
            newQuantity += cartItem.getQuantity();
        }

        if (newQuantity > product.getStockQuantity()) {
            throw new CartConflictException(
                    "Недостаточное количество товара на складе"
            );
        }

        if (cartItem == null) {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(newQuantity)
                    .build();
        } else {
            cartItem.setQuantity(newQuantity);
        }

        cartItemRepository.save(cartItem);

        return getCart(userId);
    }

    private CartItemResponse toCartItemResponse(CartItem item) {
        Product product = item.getProduct();

        BigDecimal subtotal = product.getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                product.getStockQuantity(),
                item.getQuantity(),
                subtotal
        );
    }

    public CartResponse updateItem(
            Long userId,
            Long productId,
            Integer quantity
    ) {
        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException(
                    "Количество должно быть больше нуля"
            );
        }

            Cart cart = cartRepository.findByUser_Id(userId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Корзина не найдена"
                    ));

            CartItem cartItem = cartItemRepository
                    .findByCart_IdAndProduct_Id(cart.getId(), productId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Товар не найден в корзине"
                    ));

            Product product = cartItem.getProduct();

            if (!product.isActive()) {
                throw new CartConflictException("Товар недоступен");
            }

            if (quantity > product.getStockQuantity()) {
                throw new CartConflictException(
                        "Недостаточное количество товара на складе"
                );
            }

            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);

            return getCart(userId);
    }

    public CartResponse removeItem(Long userId, Long productId) {

        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Корзина не найдена"
                ));

        CartItem cartItem = cartItemRepository
                .findByCart_IdAndProduct_Id(cart.getId(), productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Товар не найден в корзине"
                ));

        cartItemRepository.delete(cartItem);

        return getCart(userId);
    }

    public CartResponse clearCart(Long userId) {

        Cart cart = cartRepository.findByUser_Id(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Корзина не найдена"
                ));

        cartItemRepository.deleteAllByCart_Id(cart.getId());

        return getCart(userId);
    }
}
