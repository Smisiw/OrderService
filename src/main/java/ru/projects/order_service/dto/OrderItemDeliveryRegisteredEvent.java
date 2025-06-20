package ru.projects.order_service.dto;

import java.time.Instant;
import java.util.UUID;

public record OrderItemDeliveryRegisteredEvent(
        UUID orderItemId,
        String trackingNumber,
        Instant estimatedDeliveryDate
) {}
