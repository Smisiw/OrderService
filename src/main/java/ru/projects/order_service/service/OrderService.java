package ru.projects.order_service.service;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;
import ru.projects.order_service.dto.OrderItemCancelledEvent;
import ru.projects.order_service.dto.OrderRequestDto;
import ru.projects.order_service.dto.OrderResponseDto;
import ru.projects.order_service.exception.NotRelevantProductInfoException;
import ru.projects.order_service.exception.OrderNotFoundException;
import ru.projects.order_service.mapper.OrderMapper;
import ru.projects.order_service.model.Order;
import ru.projects.order_service.repository.OrderRepository;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final ProductServiceClient productServiceClient;
    private final OrderEventProducer orderEventProducer;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public void createOrder(OrderRequestDto orderRequestDto) {
        try {
            productServiceClient.checkAndReserve(orderRequestDto.orderItems());
        } catch (FeignException e) {
            throw new NotRelevantProductInfoException(e.getMessage());
        }
        Order order = orderMapper.toOrder(orderRequestDto);
        orderRepository.save(order);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                orderEventProducer.orderCreatedEventSend(orderMapper.toOrderCreatedEvent(order));
            }

            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    Set<OrderItemCancelledEvent> orderItemCancelledEvents = orderMapper.toOrderItemCancelledEventSet(orderRequestDto.orderItems());
                    orderItemCancelledEvents.forEach(orderEventProducer::orderItemCancelledEventSend);
                }
            }
        });
    }

    public OrderResponseDto getOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("Order with id " + orderId + " not found")
        );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID currentUserId = (UUID) authentication.getPrincipal();
        boolean isAdmin = authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (!isAdmin && !order.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return orderMapper.toOrderResponseDto(order);
    }
}
