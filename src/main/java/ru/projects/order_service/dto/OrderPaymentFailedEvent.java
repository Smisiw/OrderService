package ru.projects.order_service.dto;

import java.util.UUID;

public record OrderPaymentFailedEvent(
        UUID orderId,
        String reason
) {}