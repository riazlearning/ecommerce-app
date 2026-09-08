package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequestRecord;
import com.ecommerce.product.dto.ProductResponseRecord;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private UUID productId;
    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        sampleProduct = Product.builder()
                .id(productId)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .sku(1001)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("createProduct should save and return ProductResponseRecord")
    void createProduct_Success() {
        // Given
        ProductRequestRecord request = new ProductRequestRecord(
                "Test Product",
                "Test Description",
                new BigDecimal("99.99"),
                1001
        );

        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        // When
        ProductResponseRecord response = productService.createProduct(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(productId);
        assertThat(response.name()).isEqualTo("Test Product");
        assertThat(response.price()).isEqualByComparingTo(new BigDecimal("99.99"));
        assertThat(response.sku()).isEqualTo(1001);
    }

    @Test
    @DisplayName("getProductById should return ProductResponseRecord when product exists")
    void getProductById_Success() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProduct));

        // When
        ProductResponseRecord response = productService.getProductById(productId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(productId);
        assertThat(response.name()).isEqualTo("Test Product");
    }

    @Test
    @DisplayName("getProductById should throw RuntimeException when product not found")
    void getProductById_NotFound() {
        // Given
        UUID randomId = UUID.randomUUID();
        when(productRepository.findById(randomId)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> productService.getProductById(randomId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found with id: " + randomId);
    }

    @Test
    @DisplayName("getAllProducts should return list of products")
    void getAllProducts_Success() {
        // Given
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct));

        // When
        List<ProductResponseRecord> products = productService.getAllProducts();

        // Then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).id()).isEqualTo(productId);
    }

    @Test
    @DisplayName("updateProduct should modify product details and return updated response")
    void updateProduct_Success() {
        // Given
        ProductRequestRecord updateRequest = new ProductRequestRecord(
                "Updated Name",
                "Updated Desc",
                new BigDecimal("149.99"),
                1002
        );

        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        // When
        ProductResponseRecord response = productService.updateProduct(productId, updateRequest);

        // Then
        assertThat(response.name()).isEqualTo("Updated Name");
        assertThat(response.price()).isEqualByComparingTo(new BigDecimal("149.99"));
        assertThat(response.sku()).isEqualTo(1002);
    }

    @Test
    @DisplayName("deleteProduct should remove product when it exists")
    void deleteProduct_Success() {
        // Given
        when(productRepository.existsById(productId)).thenReturn(true);

        // When
        productService.deleteProduct(productId);

        // Then
        verify(productRepository).deleteById(productId);
    }
}
