package ru.projects.order_service.dto;

import ru.projects.order_service.model.OrderItemStatus;

import java.math.BigDecimal;

public record OrderItemResponseDto(
        Long id,
        Long productVariationId,
        Integer quantity,
        BigDecimal unitPrice,
        OrderItemStatus status,
        String deliveryTrackingNumber
) {
}
