package com.vendetta.ecommerce.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    public ResponseEntity<String> store(
            @RequestBody @Valid CustomerRequest request
    ) {
        return ResponseEntity.ok(service.store(request));
    }

    @PutMapping
    public ResponseEntity<Void> update(
        @RequestBody @Valid CustomerRequest request
    ) {
        service.update(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(
            @PathVariable("id") String id
    ) {
        return ResponseEntity.ok(service.existsById(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> show(
            @PathVariable("id") String id
    ) {
        return ResponseEntity.ok(service.show(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(
            @PathVariable("id") String id
    ) {
        service.destroy(id);
        return ResponseEntity.accepted().build();
    }
}
