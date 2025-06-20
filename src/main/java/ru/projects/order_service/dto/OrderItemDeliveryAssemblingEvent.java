package ru.projects.order_service.dto;

import java.util.UUID;

public record OrderItemDeliveryAssemblingEvent(
        UUID orderItemId
) {
}
