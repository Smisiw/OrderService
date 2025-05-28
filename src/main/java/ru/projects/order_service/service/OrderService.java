package ru.projects.order_service.service;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.projects.order_service.dto.OrderRequestDto;
import ru.projects.order_service.dto.OrderResponseDto;
import ru.projects.order_service.exception.NotRelevantProductInfoException;
import ru.projects.order_service.exception.OrderNotFoundException;
import ru.projects.order_service.mapper.OrderMapper;
import ru.projects.order_service.model.Order;
import ru.projects.order_service.repository.OrderRepository;

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
            //TODO: прокинуть сообшение из ответа
            throw new NotRelevantProductInfoException(e.getMessage());
        }
        Order order = orderMapper.toOrder(orderRequestDto);
        orderRepository.save(order);
        orderEventProducer.send(orderMapper.toOrderCreatedEvent(order));
    }

    public OrderResponseDto getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("Order with id " + orderId + " not found")
        );
        return orderMapper.toOrderResponseDto(order);
    }


}
