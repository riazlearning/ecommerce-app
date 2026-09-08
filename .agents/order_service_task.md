Create a new Maven-based Spring Boot 3.4 microservice named "order-service" targeting Java 21 under the root package "com.ecommerce.order".

### Dependencies (pom.xml)
Include these exact dependencies:
- Spring Web (`spring-boot-starter-web`)
- Spring Data JPA (`spring-boot-starter-data-jpa`)
- PostgreSQL Driver (`postgresql`)
- Validation (`spring-boot-starter-validation`)
- Spring Boot Actuator (`spring-boot-starter-actuator`)

---

### Configuration (src/main/resources/application.yml)
Configure `application.yml` with the following:
- Server port: `8082` (to prevent conflicts with product-service on `8081`)
- Application name: `order-service`
- PostgreSQL datasource pointing to `jdbc:postgresql://localhost:5432/order_db`
- Hibernate `ddl-auto` set to `update` (do NOT use `create-drop`)
- Show SQL enabled (`spring.jpa.show-sql: true`)

---

### Data Architecture & Implementation

Create the following files in `com.ecommerce.order`:

1. **Entity (`model/Order.java` & `model/OrderStatus.java`):**
   - `OrderStatus` Enum: `PENDING`, `COMPLETED`, `CANCELLED`
   - `Order` JPA Entity mapped to table `orders`:
     - `id` (UUID, primary key)
     - `customerId` (UUID, mandatory)
     - `productId` (UUID, mandatory)
     - `quantity` (Integer, mandatory, minimum 1)
     - `totalPrice` (BigDecimal, mandatory)
     - `status` (Enumerated STRING: OrderStatus)
     - `createdAt` (Instant, defaults to current UTC time)

2. **DTO Records (`dto/`):**
   - `OrderRequest`: `customerId` (UUID, `@NotNull`), `productId` (UUID, `@NotNull`), `quantity` (Integer, `@NotNull`, `@Min(1)`), `totalPrice` (BigDecimal, `@NotNull`, `@Positive`)
   - `OrderResponse`: `id`, `customerId`, `productId`, `quantity`, `totalPrice`, `status`, `createdAt`

3. **Repository (`repository/OrderRepository.java`):**
   - Spring Data `JpaRepository<Order, UUID>`
   - Custom query method: `List<Order> findByCustomerId(UUID customerId)`

4. **Service Layer (`service/OrderService.java` & `service/impl/OrderServiceImpl.java`):**
   - `createOrder(OrderRequest request)`: Maps DTO to entity, sets status to `PENDING`, saves, and returns `OrderResponse`.
   - `getOrderById(UUID id)`: Returns `OrderResponse` or throws exception if not found.
   - `getOrdersByCustomer(UUID customerId)`: Returns list of orders for a given customer.
   - `updateOrderStatus(UUID id, OrderStatus status)`: Updates and returns the updated order.

5. **Controller Layer (`controller/OrderController.java`):**
   - Base mapping: `/api/orders`
   - `POST /api/orders`: `@Valid @RequestBody OrderRequest` $\rightarrow$ returns HTTP `201 Created`
   - `GET /api/orders/{id}`: Returns HTTP `200 OK`
   - `GET /api/orders/customer/{customerId}`: Returns HTTP `200 OK`
   - `PATCH /api/orders/{id}/status`: Query param `status` $\rightarrow$ updates status and returns HTTP `200 OK`

---

### Execution Instructions
1. Scaffold the project and code structure.
2. Verify all imports and annotations (`@RestController`, `@RequestMapping`, `@Valid`, `@RequestBody`, `@Entity`, `@Table`).
3. Run `mvn clean compile` to ensure zero build or syntax errors.