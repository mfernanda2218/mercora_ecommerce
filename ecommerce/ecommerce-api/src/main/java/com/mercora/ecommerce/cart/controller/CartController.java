package com.mercora.ecommerce.cart.controller;

import com.mercora.ecommerce.cart.dto.CartItemRequest;
import com.mercora.ecommerce.cart.entity.Cart;
import com.mercora.ecommerce.cart.entity.CartItem;
import com.mercora.ecommerce.cart.repository.CartItemRepository;
import com.mercora.ecommerce.cart.repository.CartRepository;
import com.mercora.ecommerce.products.entity.Product;
import com.mercora.ecommerce.products.repository.ProductRepository;
import com.mercora.ecommerce.users.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });
    }

    @GetMapping
    public ResponseEntity<Cart> getCart() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(getOrCreateCart(user));
    }

    @PostMapping("/items")
    public ResponseEntity<Cart> addItem(@Valid @RequestBody CartItemRequest request) {
        User user = getAuthenticatedUser();
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
        }

        return ResponseEntity.ok(cartRepository.findById(cart.getId()).get());
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<Cart> updateItemQuantity(@PathVariable UUID itemId, @RequestParam int quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        
        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return ResponseEntity.ok(item.getCart());
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Cart> removeItem(@PathVariable UUID itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        Cart cart = item.getCart();
        cartItemRepository.delete(item);
        
        return ResponseEntity.ok(cartRepository.findById(cart.getId()).get());
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        User user = getAuthenticatedUser();
        cartRepository.findByUserId(user.getId()).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
        return ResponseEntity.ok().build();
    }
}
