package ru.projects.order_service.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private UUID userId;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private Instant createdAt;
    private String deliveryAddress;
    private String paymentMethod;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<OrderItem> items = new HashSet<>();
}
