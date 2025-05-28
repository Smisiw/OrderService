package ru.projects.order_service.dto;

public record OrderPaymentFailedEvent(
        Long orderId,
        String reason
) {}