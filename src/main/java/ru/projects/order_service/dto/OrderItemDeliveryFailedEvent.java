package ru.projects.order_service.dto;

public record OrderItemDeliveryFailedEvent(
        Long orderItemId,
        String reason
) {}
