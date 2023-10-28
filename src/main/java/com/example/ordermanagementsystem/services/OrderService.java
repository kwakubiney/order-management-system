package com.example.ordermanagementsystem.services;

import com.example.ordermanagementsystem.Payload.GenericMessage;
import com.example.ordermanagementsystem.Payload.OrderPayload;
import com.example.ordermanagementsystem.Payload.ProductPayload;
import com.example.ordermanagementsystem.input.CreateOrderInput;
import com.example.ordermanagementsystem.input.UpdateOrderInput;

import java.util.List;

public interface OrderService {
    OrderPayload createOrder(CreateOrderInput input);
    OrderPayload updateOrder(UpdateOrderInput input);
    GenericMessage deleteOrder(Long id);
    OrderPayload order(Long id);
    List<OrderPayload> orders();
    List<OrderPayload> ordersByUserId(Long id);
    List<ProductPayload> productsByOrderId(Long id);
}
