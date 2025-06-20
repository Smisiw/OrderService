package ru.projects.order_service.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = "order")
@ToString(exclude = "order")
public class OrderItem {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false)
    private UUID productVariationId;
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false)
    private BigDecimal unitPrice;
    @Column(nullable = false)
    private OrderItemStatus status;
    private String deliveryTrackingNumber;
    private Instant estimatedDeliveryDate;
    private Instant deliveryDate;
    private String cancellationReason;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
