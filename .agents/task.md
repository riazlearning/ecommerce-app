Create a new Maven-based Spring Boot 3.4 microservice named "product-service" with Java 21 under the root package "com.ecommerce.product".

Include these exact dependencies in pom.xml:
- Spring Web (spring-boot-starter-web)
- Spring Data JPA (spring-boot-starter-data-jpa)
- PostgreSQL Driver (postgresql)
- Validation (spring-boot-starter-validation)
- Spring Boot Actuator (spring-boot-starter-actuator)

Configure application.yml for a database named 'product_db' running on localhost:5432 with standard PostgreSQL settings.


In com.ecommerce.product, implement the Product features using best practices:

1. Entity (model/Product.java):
   - id (UUID)
   - name (String, mandatory)
   - description (String)
   - price (BigDecimal, non-negative)
   - sku (Integer)
   - createdAt (Instant)

2. DTOs (dto/):
   - ProductRequestRecord (with @NotNull and @Positive validation annotations)
   - ProductResponseRecord

3. Layers:
   - ProductRepository (JpaRepository interface)
   - ProductService (Interface + Implementation handling creation, lookup, and updates)
   - ProductController (@RestController mapping endpoints to /api/products)