package ru.projects.order_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.projects.order_service.dto.OrderCreatedEvent;
import ru.projects.order_service.dto.OrderItemCancelledEvent;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void orderCreatedEventSend(OrderCreatedEvent orderCreatedEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(orderCreatedEvent);
            kafkaTemplate.send("order.created", json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void orderItemCancelledEventSend(OrderItemCancelledEvent orderItemCancelledEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(orderItemCancelledEvent);
            kafkaTemplate.send("order.item.cancelled", json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
