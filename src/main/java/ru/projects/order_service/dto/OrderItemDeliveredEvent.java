package ru.projects.order_service.dto;

import java.time.Instant;

public record OrderItemDeliveredEvent(
        Long orderItemId,
        Instant deliveredAt
) {
}
