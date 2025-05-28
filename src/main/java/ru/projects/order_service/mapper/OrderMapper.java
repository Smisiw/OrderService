package ru.projects.order_service.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.projects.order_service.dto.*;
import ru.projects.order_service.model.Order;
import ru.projects.order_service.model.OrderItem;

import java.math.BigDecimal;
import java.time.Instant;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "items", source = "orderItems")
    public abstract Order toOrder(OrderRequestDto orderRequestDto);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "RESERVED")
    @Mapping(target = "deliveryTrackingNumber", ignore = true)
    @Mapping(target = "order", ignore = true)
    public abstract OrderItem toOrderItem(OrderItemRequestDto orderItemRequestDto);
    @Mapping(target = "orderId", source = "id")
    public abstract OrderCreatedEvent toOrderCreatedEvent(Order order);
    public abstract OrderCreatedEvent.Item toOrderCreatedEventItem(OrderItem orderItem);
    public abstract OrderResponseDto toOrderResponseDto(Order order);
    public abstract OrderItemResponseDto toOrderItemResponseDto(OrderItem orderItem);
    @AfterMapping
    protected void afterMapping(@MappingTarget Order order, OrderRequestDto orderRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(authentication.getPrincipal().toString());
        order.setUserId(userId);
        order.setCreatedAt(Instant.now());
        BigDecimal totalPrice = order.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(totalPrice);
        order.getItems().forEach(orderItem -> orderItem.setOrder(order));
    }
}
