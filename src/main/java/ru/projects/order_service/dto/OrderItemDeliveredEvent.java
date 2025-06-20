package ru.projects.order_service.dto;

import java.time.Instant;
import java.util.UUID;

public record OrderItemDeliveredEvent(
        UUID orderItemId,
        Instant deliveredAt
) {
}
