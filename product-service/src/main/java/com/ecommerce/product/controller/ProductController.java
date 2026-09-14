package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductRequestRecord;
import com.ecommerce.product.dto.ProductResponseRecord;
import com.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseRecord> createProduct(@Valid @RequestBody ProductRequestRecord request) {
        ProductResponseRecord response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseRecord>> getAllProducts(
        @RequestHeader(value = "X-Gateway-Request-ID", required = false)
        String gatewayRequestId) {

    System.out.println(
            "Gateway Request ID received: " + gatewayRequestId
    );

    // existing logic
    return ResponseEntity.ok(productService.getAllProducts());
}

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseRecord> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
}

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseRecord> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequestRecord request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
