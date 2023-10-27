package com.example.ordermanagementsystem.controller;

import com.example.ordermanagementsystem.Payload.GenericMessage;
import com.example.ordermanagementsystem.Payload.OrderPayload;
import com.example.ordermanagementsystem.Payload.ProductPayload;
import com.example.ordermanagementsystem.input.CreateOrderInput;
import com.example.ordermanagementsystem.input.UpdateOrderInput;
import com.example.ordermanagementsystem.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OrderController{

    private final OrderService orderService;

    @MutationMapping
    public OrderPayload createOrder(@Argument(name = "input") CreateOrderInput input) {
        return orderService.createOrder(input);
    }

    @MutationMapping
    public OrderPayload updateOrder(@Argument(name = "input") UpdateOrderInput input) {
        return orderService.updateOrder(input);
    }

    @MutationMapping
    public GenericMessage deleteOrder(@Argument(name = "id") Long id) {
        return orderService.deleteOrder(id);
    }

    @QueryMapping
    public OrderPayload order(@Argument(name = "id") Long id) {
        return orderService.order(id);
    }

    @QueryMapping
    public List<OrderPayload> orders() {
        return orderService.orders();
    }
    @QueryMapping
    public List<OrderPayload> ordersByUserId(@Argument(name = "id") Long id) {
        return orderService.ordersByUserId(id);
    }

    @QueryMapping
    public List<ProductPayload> productsByOrderId(@Argument(name = "id") Long id) {
        return orderService.productsByOrderId(id);
    }
}