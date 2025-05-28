package ru.projects.order_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.projects.order_service.dto.OrderItemRequestDto;

import java.util.Set;

@FeignClient(name = "PRODUCT-SERVICE", path = "/api/products")
public interface ProductServiceClient {

    @PostMapping("/checkAndReserve")
    ResponseEntity<String> checkAndReserve(@RequestBody Set<OrderItemRequestDto> orderItems);
}
