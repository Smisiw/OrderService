package ru.projects.order_service.dto;

import java.time.Instant;

public record OrderPaidEvent(
        Long orderId,
        Instant paidAt,
        String paymentReference
) {}
