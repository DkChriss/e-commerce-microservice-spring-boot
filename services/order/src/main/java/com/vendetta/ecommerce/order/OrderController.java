package com.vendetta.ecommerce.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @PostMapping
    public ResponseEntity<Integer> store(
       @RequestBody @Valid OrderRequest request
    ) {
        return ResponseEntity.ok(service.store(request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> show(
            @PathVariable("{id}") Integer id
    ) {
        return ResponseEntity.ok(service.show(id));
    }



}
