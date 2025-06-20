package ru.projects.order_service.dto;

import java.util.UUID;

public record OrderItemDeliveryFailedEvent(
        UUID orderItemId,
        String reason
) {}
