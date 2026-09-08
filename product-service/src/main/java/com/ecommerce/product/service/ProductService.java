package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequestRecord;
import com.ecommerce.product.dto.ProductResponseRecord;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponseRecord createProduct(ProductRequestRecord request);
    List<ProductResponseRecord> getAllProducts();
    ProductResponseRecord getProductById(UUID id);
    ProductResponseRecord updateProduct(UUID id, ProductRequestRecord request);
    void deleteProduct(UUID id);
}
