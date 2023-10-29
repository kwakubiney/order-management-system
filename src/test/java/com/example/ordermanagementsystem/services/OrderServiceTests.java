package com.example.ordermanagementsystem.services;



import com.auth0.jwt.algorithms.Algorithm;
import com.example.ordermanagementsystem.config.EntityMapper;
import com.example.ordermanagementsystem.entity.Order;
import com.example.ordermanagementsystem.entity.Product;
import com.example.ordermanagementsystem.entity.ProductLine;
import com.example.ordermanagementsystem.entity.User;
import com.example.ordermanagementsystem.exception.CustomGraphQLException;
import com.example.ordermanagementsystem.input.CreateOrderInput;
import com.example.ordermanagementsystem.input.OrderItemInput;
import com.example.ordermanagementsystem.input.UpdateOrderInput;
import com.example.ordermanagementsystem.payload.OrderPayload;
import com.example.ordermanagementsystem.repository.OrderRepository;
import com.example.ordermanagementsystem.repository.ProductLineRepository;
import com.example.ordermanagementsystem.repository.ProductRepository;
import com.example.ordermanagementsystem.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductLineRepository productLineRepository;

    @Mock
    private EntityMapper entityMapper;

    private Product product;
    private Order order;

    private User user;

    //TODO: Find appropriate way to handle auth when testing services
    private JwtAuthenticationToken auth;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private ProductService productService;

    @BeforeEach
    public void init() {
        product = new Product(1L, "Nike", 10,  50D);
        user =  new User(1L, "kwaku", "k@mail.com", "1234", User.Role.ADMIN, null);
        order = new Order(1L, null, user);
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", Algorithm.HMAC512("1234"))
                .claim("email", "https://idp.example.org")
                .subject("k@mail.com")
                .build();
        auth = new JwtAuthenticationToken(jwt);
    }
    @Test
    void createOrder_GivenUserId_ShouldSucceed() {
        List<OrderItemInput> orderInputs = new ArrayList<>();
        List<ProductLine> productLines = new ArrayList<>();
        productLines.add(new ProductLine(1L, product, 5, order));
        var savedOrderPayload = new OrderPayload(order.getId(), productLines, user);
        Mockito.when(userRepository.findUserByEmail(any())).thenReturn(Optional.of(user));
        var item = OrderItemInput.builder().quantity(2).productId(1L).build();
        orderInputs.add(item);
        Mockito.when(productRepository.findProductById(1L))
                .thenReturn(Optional.of(product));
        Mockito.when(orderRepository.save(any()))
                .thenReturn(order);
        Mockito.when(productLineRepository.saveAll(any()))
                .thenReturn(productLines);
        Mockito.when(entityMapper.orderToOrderPayload(any()))
                .thenReturn(new OrderPayload(order.getId(), productLines, user));
        CreateOrderInput orderInput = CreateOrderInput.builder()
                        .userId(1L)
                        .items(orderInputs)
                        .build();
        var savedOrder = orderService.createOrder(orderInput, auth);
        Assertions.assertThat(savedOrder).isEqualTo
                (savedOrderPayload);
    }

    @Test
    void updateOrderWithLessProductQuantity_GivenUserId_ShouldSucceedAndIncreaseStock() {
        List<OrderItemInput> updatedOrderInputs = new ArrayList<>();
        List<ProductLine> productLines = new ArrayList<>();
        productLines.add(new ProductLine(1L, product, 5, order));
        var orderWithProductLines = new Order(1L, productLines, user);
        var updatedItem =  OrderItemInput.builder().quantity(7).productId(1L).build();
        updatedOrderInputs.add(updatedItem);
        Mockito.when(userRepository.findUserByEmail(any())).thenReturn(Optional.of(user));
        Mockito.when(productRepository.findProductById(1L))
                .thenReturn(Optional.of(product));
        Mockito.when(orderRepository.findOrderById(any()))
                .thenReturn(Optional.of(orderWithProductLines));
        Mockito.when(orderRepository.save(any()))
                .thenReturn(order);
        UpdateOrderInput orderInput = UpdateOrderInput.builder()
                .userId(1L)
                .id(order.getId())
                .items(updatedOrderInputs)
                .build();
        var updatedOrder = orderService.updateOrder(orderInput, auth);
        //Verify that stock updates properly
        Assertions.assertThat(updatedOrder.getProducts().get(0).getProduct().getStock()).isEqualTo
                (8);
    }

    @Test
    void updateOrderWithMoreProductQuantity_GivenUserId_ShouldSucceedAndDecreaseStock() {
        List<OrderItemInput> updatedOrderInputs = new ArrayList<>();
        List<ProductLine> productLines = new ArrayList<>();
        List<ProductLine> updatedProductLines = new ArrayList<>();
        var updatedProduct = new Product(1L, "Nike", 12,  50D);
        productLines.add(new ProductLine(1L, product, 5, order));
        updatedProductLines.add(new ProductLine(1L, updatedProduct, 3, order));
        var orderWithProductLines = new Order(1L, productLines, user);
        var updatedItem =  OrderItemInput.builder().quantity(3).productId(1L).build();
        updatedOrderInputs.add(updatedItem);
        Mockito.when(userRepository.findUserByEmail(any())).thenReturn(Optional.of(user));
        Mockito.when(productRepository.findProductById(1L))
                .thenReturn(Optional.of(product));
        Mockito.when(orderRepository.findOrderById(any()))
                .thenReturn(Optional.of(orderWithProductLines));
        Mockito.when(orderRepository.save(any()))
                .thenReturn(order);
        UpdateOrderInput orderInput = UpdateOrderInput.builder()
                .userId(1L)
                .id(order.getId())
                .items(updatedOrderInputs)
                .build();
        var updatedOrder = orderService.updateOrder(orderInput, auth);
        Assertions.assertThat(updatedOrder.getProducts().get(0).getProduct().getStock()).isEqualTo
                (updatedProductLines.get(0).getProduct().getStock());
    }

    @Test
    void createOrderWithMoreProductQuantityThanStock_GivenUserId_ShouldFailAndThrowException() {
        List<OrderItemInput> orderInputs = new ArrayList<>();
        Mockito.when(userRepository.findUserByEmail(any())).thenReturn(Optional.of(user));
        var item = OrderItemInput.builder().quantity(12).productId(1L).build();
        orderInputs.add(item);
        Mockito.when(productRepository.findProductById(1L))
                .thenReturn(Optional.of(product));
        CreateOrderInput orderInput = CreateOrderInput.builder()
                .userId(1L)
                .items(orderInputs)
                .build();
        Assertions.assertThatThrownBy(() -> {
                    orderService.createOrder(orderInput, auth);
                }).isInstanceOf(CustomGraphQLException.class)
                .hasMessage(String.format("Product %s does not have enough stock to fulfill the order", product.getName()));
    }
}