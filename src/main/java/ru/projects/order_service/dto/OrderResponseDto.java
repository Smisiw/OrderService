package ru.projects.order_service.dto;

import ru.projects.order_service.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record OrderResponseDto(
        UUID id,
        OrderStatus status,
        BigDecimal totalPrice,
        Instant createdAt,
        String deliveryAddress,
        String paymentMethod,
        Set<OrderItemResponseDto> items
) {
}
