package ru.projects.order_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemRequestDto(
        UUID productVariationId,
        Integer quantity,
        BigDecimal unitPrice
) {
}
