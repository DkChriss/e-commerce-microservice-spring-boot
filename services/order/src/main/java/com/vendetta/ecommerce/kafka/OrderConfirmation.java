package com.vendetta.ecommerce.kafka;

import com.vendetta.ecommerce.customer.CustomerResponse;
import com.vendetta.ecommerce.order.PaymentMethod;
import com.vendetta.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
