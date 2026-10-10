package com.mercora.ecommerce.checkout.dto;

import com.mercora.ecommerce.addresses.entity.Address;
import com.mercora.ecommerce.cart.entity.Cart;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CheckoutSummaryResponse {
    private Cart cart;
    private Address address;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal total;
}
