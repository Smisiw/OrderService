package ru.projects.order_service.dto;

import java.time.Instant;
import java.util.UUID;

public record OrderPaidEvent(
        UUID orderId,
        Instant paidAt,
        String paymentReference
) {}
