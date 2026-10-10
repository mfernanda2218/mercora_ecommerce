package com.mercora.ecommerce.checkout.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CheckoutSummaryRequest {
    private UUID addressId;
}
