package ru.projects.order_service.dto;

import java.time.Instant;

public record OrderItemDeliveryRegisteredEvent(
        Long orderItemId,
        String trackingNumber,
        Instant estimatedDeliveryDate
) {}
