package ru.projects.order_service.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = "order")
@ToString(exclude = "order")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long productVariationId;
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false)
    private BigDecimal unitPrice;
    @Column(nullable = false)
    private OrderItemStatus status;
    private String deliveryTrackingNumber;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
