package com.vendetta.ecommerce.order;

import com.vendetta.ecommerce.customer.CustomerClient;
import com.vendetta.ecommerce.exception.BusinessException;
import com.vendetta.ecommerce.kafka.OrderConfirmation;
import com.vendetta.ecommerce.kafka.OrderProducer;
import com.vendetta.ecommerce.orderline.OrderLineRequest;
import com.vendetta.ecommerce.orderline.OrderLineService;
import com.vendetta.ecommerce.payment.PaymentClient;
import com.vendetta.ecommerce.payment.PaymentRequest;
import com.vendetta.ecommerce.product.ProductClient;
import com.vendetta.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;

    public Integer store(OrderRequest request) {
        var customer = this.customerClient.show(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));

        var purchasedProducts = this.productClient.purchaseProduct(request.products());

        var order = this.repository.save(mapper.toOrder(request));

        for (PurchaseRequest purchaseRequest: request.products()) {
            orderLineService.store(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        orderProducer.sendOrderConfirmation( //SEND PRODUCER MESSAGE ON KAFKA
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> list() {
        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse show(Integer id) {
        return repository.findById(id)
                .map(mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
    }
}
