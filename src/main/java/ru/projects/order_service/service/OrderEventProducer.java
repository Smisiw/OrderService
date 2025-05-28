package ru.projects.order_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.projects.order_service.dto.OrderCreatedEvent;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(OrderCreatedEvent orderCreatedEvent) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(orderCreatedEvent);
            kafkaTemplate.send("order.created", json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
