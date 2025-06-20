package ru.projects.order_service.dto;

import ru.projects.order_service.model.OrderItemStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponseDto(
        UUID id,
        UUID productVariationId,
        Integer quantity,
        BigDecimal unitPrice,
        OrderItemStatus status,
        String deliveryTrackingNumber
) {
}
