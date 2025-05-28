package ru.projects.order_service.dto;

import java.math.BigDecimal;

public record OrderItemRequestDto(
        Long productVariationId,
        Integer quantity,
        BigDecimal unitPrice
) {
}
