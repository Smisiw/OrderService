package ru.projects.order_service.dto;

import java.util.Set;

public record OrderRequestDto(
        Set<OrderItemRequestDto> orderItems,
        String paymentMethod,
        String deliveryAddress
) {
}
