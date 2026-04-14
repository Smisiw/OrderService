package ru.projects.order_service.service;

import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.projects.order_service.dto.*;
import ru.projects.order_service.exception.NotRelevantProductInfoException;
import ru.projects.order_service.exception.OrderNotFoundException;
import ru.projects.order_service.mapper.OrderMapper;
import ru.projects.order_service.model.Order;
import ru.projects.order_service.model.OrderStatus;
import ru.projects.order_service.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ProductServiceClient productServiceClient;
    @Mock
    private OrderEventProducer orderEventProducer;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private final UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID orderId = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @BeforeEach
    void setUp() {
        var auth = new UsernamePasswordAuthenticationToken(userId, null, Set.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void getOrder_returnsOrderResponseDto_whenFound() {
        Order order = new Order();
        order.setId(orderId);
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);

        OrderResponseDto responseDto = new OrderResponseDto(orderId, OrderStatus.PENDING, null, null, null, null, Set.of());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toOrderResponseDto(order)).thenReturn(responseDto);

        OrderResponseDto result = orderService.getOrder(orderId);

        assertEquals(orderId, result.id());
        assertEquals(OrderStatus.PENDING, result.status());
    }

    @Test
    void getOrder_throws_whenNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(orderId));
    }

    @Test
    void createOrder_throwsNotRelevantProductInfoException_onFeignError() {
        OrderItemRequestDto item = new OrderItemRequestDto(UUID.randomUUID(), 1, BigDecimal.TEN);
        OrderRequestDto requestDto = new OrderRequestDto(Set.of(item), "CARD", "Street 1");

        doThrow(mock(FeignException.class)).when(productServiceClient).checkAndReserve(any());

        assertThrows(NotRelevantProductInfoException.class, () -> orderService.createOrder(requestDto));
        verify(orderRepository, never()).save(any());
    }
}
