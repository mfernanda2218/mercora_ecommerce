package com.mercora.ecommerce.checkout.controller;

import com.mercora.ecommerce.addresses.entity.Address;
import com.mercora.ecommerce.addresses.repository.AddressRepository;
import com.mercora.ecommerce.cart.entity.Cart;
import com.mercora.ecommerce.cart.entity.CartItem;
import com.mercora.ecommerce.cart.repository.CartRepository;
import com.mercora.ecommerce.checkout.dto.CheckoutSummaryRequest;
import com.mercora.ecommerce.checkout.dto.CheckoutSummaryResponse;
import com.mercora.ecommerce.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    @PostMapping("/summary")
    public ResponseEntity<?> getSummary(@RequestBody CheckoutSummaryRequest request) {
        User user = getAuthenticatedUser();
        
        Optional<Cart> cartOpt = cartRepository.findByUserId(user.getId());
        if (cartOpt.isEmpty() || cartOpt.get().getItems().isEmpty()) {
            return ResponseEntity.badRequest().body("Cart is empty");
        }
        
        Cart cart = cartOpt.get();
        
        // Validate stock
        for (CartItem item : cart.getItems()) {
            if (item.getQuantity() > item.getProduct().getStock()) {
                return ResponseEntity.badRequest().body("Product " + item.getProduct().getName() + " has insufficient stock");
            }
        }
        
        Address address = null;
        if (request.getAddressId() != null) {
            address = addressRepository.findById(request.getAddressId()).orElse(null);
            if (address == null || !address.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest().body("Invalid address");
            }
        }

        BigDecimal subtotal = cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal shippingCost = BigDecimal.valueOf(15.00); // Simulated shipping cost
        BigDecimal total = subtotal.add(shippingCost);

        CheckoutSummaryResponse response = CheckoutSummaryResponse.builder()
                .cart(cart)
                .address(address)
                .subtotal(subtotal)
                .shippingCost(shippingCost)
                .total(total)
                .build();

        return ResponseEntity.ok(response);
    }
}
