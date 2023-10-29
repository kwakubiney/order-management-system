package com.example.ordermanagementsystem.services;

import com.example.ordermanagementsystem.payload.GenericMessage;
import com.example.ordermanagementsystem.payload.OrderPayload;
import com.example.ordermanagementsystem.payload.ProductPayload;
import com.example.ordermanagementsystem.input.CreateOrderInput;
import com.example.ordermanagementsystem.input.UpdateOrderInput;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface OrderService {
    OrderPayload createOrder(CreateOrderInput input, Authentication authentication);
    OrderPayload updateOrder(UpdateOrderInput input, Authentication authentication);
    GenericMessage deleteOrder(Long id);
    OrderPayload order(Long id);
    List<OrderPayload> orders();
    List<OrderPayload> ordersByUserId(Long id);
    List<ProductPayload> productsByOrderId(Long id);
}
