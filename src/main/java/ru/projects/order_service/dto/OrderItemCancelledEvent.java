package ru.projects.order_service.dto;

public record OrderItemCancelledEvent(
        Long orderItemId,
        String reason
) {}
