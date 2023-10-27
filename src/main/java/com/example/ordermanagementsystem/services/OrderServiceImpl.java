package com.example.ordermanagementsystem.services;

import com.example.ordermanagementsystem.Dto.GenericMessage;
import com.example.ordermanagementsystem.Dto.OrderPayload;
import com.example.ordermanagementsystem.Dto.ProductPayload;
import com.example.ordermanagementsystem.config.EntityMapper;
import com.example.ordermanagementsystem.entity.Order;
import com.example.ordermanagementsystem.entity.Product;
import com.example.ordermanagementsystem.entity.ProductLine;
import com.example.ordermanagementsystem.entity.User;
import com.example.ordermanagementsystem.exception.CustomGraphQLException;
import com.example.ordermanagementsystem.input.CreateOrderInput;
import com.example.ordermanagementsystem.input.OrderItemInput;
import com.example.ordermanagementsystem.input.UpdateOrderInput;
import com.example.ordermanagementsystem.input.UpdateProductInput;
import com.example.ordermanagementsystem.repository.OrderRepository;
import com.example.ordermanagementsystem.repository.ProductLineRepository;
import com.example.ordermanagementsystem.repository.ProductRepository;
import com.example.ordermanagementsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductLineRepository productLineRepository;
    private final EntityMapper entityMapper;
    private final ProductService productService;
    @Override
    public OrderPayload createOrder(CreateOrderInput input) {
        Optional<User> existingUser = userRepository.findUserById(input.getUserId());
        if (existingUser.isEmpty()){
            throw new CustomGraphQLException(String.format("User with id %s does not exist", input.getUserId()), 404);
        }
        var productQuantityMap = validateCreateOrderInput(input);

        Order orderToBeSaved = Order.builder().users(existingUser.get())
                .build();
        Order savedOrder = orderRepository.save(orderToBeSaved);
        List<ProductLine> productLines = new ArrayList<>();
        productQuantityMap.forEach((product, quantity) -> {
            ProductLine productLine = ProductLine.builder()
                    .product(product)
                    .quantity(quantity)
                    .order(savedOrder)
                    .build();
            productLines.add(productLine);
            productService.updateProduct( UpdateProductInput.builder().id(product.getId())
                    .stock(product.getStock()-quantity)
                    .price(product.getPrice())
                    .name(product.getName())
                    .build());
        });
        productLineRepository.saveAll(productLines);
        //TODO: Handle null products response
        return entityMapper.orderToOrderPayload(savedOrder);
    }

    //TODO: Only owners of order should be allowed to update
    @Override
    public OrderPayload updateOrder(UpdateOrderInput input) {
        Optional<Order> existingOrder = orderRepository.findOrderById(input.getId());
        if (existingOrder.isEmpty()){
            throw new CustomGraphQLException(String.format("Order with id %s does not exist", input.getId()), 404);
        }
        Map<Long, Long> productLineIdToProductId = new HashMap<>();
        List<ProductLine> existingProductLine = existingOrder.get().getProducts();
        Map<Long, Integer> existingProductOrderToQuantity = new HashMap<>();
        existingProductLine.forEach(
                (element) -> {
                    existingProductOrderToQuantity.put(element.getProduct().getId(), element.getQuantity());
                }
        );
        var listOfExistingProductIdsAndQuantities = new ArrayList<OrderItemInput>();
        existingProductLine.forEach(
                (element) ->{
                    var product_id = element.getProduct().getId();
                    var product_quantity = element.getQuantity();
                    productLineIdToProductId.put(product_id, element.getId());
                    listOfExistingProductIdsAndQuantities.add(new OrderItemInput(product_quantity, product_id));
                }
        );

        validateUpdateOrderInput(input);

        //Construct map of the resultant between new orders and existing orders
        var mapOfResultantProducts = new HashMap<Long, Integer>();
        for (OrderItemInput item : listOfExistingProductIdsAndQuantities) {
            Long productId = item.getProductId();
            int quantity = item.getQuantity();
            mapOfResultantProducts.put(productId, quantity);
        }
        for (OrderItemInput item : input.getItems()) {
            Long productId = item.getProductId();
            int quantity = item.getQuantity();
            if (mapOfResultantProducts.containsKey(productId)) {
                mapOfResultantProducts.put(productId, quantity);
            } else {
                mapOfResultantProducts.put(productId, quantity);
            }
        }

        //Update the necessary product lines on order update
        List<ProductLine> productLines = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        Map<Long, Product> productIdToProduct = new HashMap<>();
        mapOfResultantProducts.forEach(
                (productId, quantity) -> {
                    var product = productRepository.findProductById(productId).get();
                    productIdToProduct.put(productId, product);
                    products.add(product);
                    var productLine= ProductLine.builder().product(product)
                            .order(existingOrder.get())
                            .quantity(quantity)
                            .id(productLineIdToProductId.get(productId)).build();
                    productLineRepository.save(productLine);
                    productLines.add(productLine);
                }
        );
        var updatedOrder = orderRepository.save(existingOrder.get());

        //Computing and updating stock values after order updates relative to previous state.
        mapOfResultantProducts.forEach(
                (productId, newQuantity) -> {
                    //product already exists
                    if (productLineIdToProductId.containsKey(productId)){
                        Integer previousQuantity = existingProductOrderToQuantity.get(productId);
                        var productCurrentStock = productRepository.findProductById(productId).get().getStock();
                        if (previousQuantity - newQuantity > 0){
                            productIdToProduct.get(productId).setStock(productCurrentStock + (previousQuantity - newQuantity));
                        }else{
                            productIdToProduct.get(productId).setStock(productCurrentStock - (newQuantity - previousQuantity));
                        }
                    }else {
                        var product = productRepository.findProductById(productId).get();
                        product.setStock(product.getStock() - newQuantity);
                        productRepository.save(product);
                    }
                    productRepository.saveAll(new ArrayList<>(productIdToProduct.values()));
                }
        );
        return OrderPayload.builder().user(updatedOrder.getUsers())
                .products(productLines)
                .id(updatedOrder.getId()).build();
    }

    //TODO: Only owners of order should be allowed to update
    @Override
    public GenericMessage deleteOrder(Long id) {
        Optional<Order> existingOrder = orderRepository.findOrderById(id);
        if (existingOrder.isEmpty()){
            throw new CustomGraphQLException(String.format("Order with id %s does not exist", id), 404);
        }
        Map<Long, Long> productLineIdToProductId = new HashMap<>();
        List<ProductLine> existingProductLine = existingOrder.get().getProducts();
        Map<Product, Integer> existingProductOrderToQuantity = new HashMap<>();
        existingProductLine.forEach(
                (element) -> {
                    existingProductOrderToQuantity.put(element.getProduct(), element.getQuantity());
                }
        );
        existingProductOrderToQuantity.forEach(
                (product, quantity) -> {
                    product.setStock(product.getStock() + quantity);
                    productRepository.save(product);
                }
        );
        orderRepository.deleteById(id);
        //TODO: For deletion, iterate through deleted items and increase their stock
        return new GenericMessage(String.format("Order with id %s has successfully been deleted", id));
    }

    @Override
    public OrderPayload order(Long id) {
        Optional<Order> existingOrder = orderRepository.findOrderById(id);
        if (existingOrder.isEmpty()){
            throw new CustomGraphQLException(String.format("Order with id %s does not exist", id), 404);
        }
        return entityMapper.orderToOrderPayload(existingOrder.get());
    }

    @Override
    public List<OrderPayload> orders() {
        return orderRepository.findAll().stream().map(
                (order)-> OrderPayload.builder().
                        id(order.getId())
                        .products(order.getProducts())
                        .user(order.getUsers())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public List<OrderPayload> ordersByUserId(Long id) {
        return orderRepository.findByUsers_Id(id).stream().map(
                (order)-> OrderPayload.builder().
                        id(order.getId())
                        .products(order.getProducts())
                        .user(order.getUsers())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public List<ProductPayload> productsByOrderId(Long id) {
        List<Product> products = new ArrayList<>();
        orderRepository.findByUsers_Id(id).forEach(order -> {
            List<ProductLine> orderProducts = order.getProducts();
            for (ProductLine product : orderProducts) {
                products.add(product.getProduct());
            }
        });
        return products.stream().map(
                entityMapper::productToProductPayload
        ).collect(Collectors.toList());
    }

    public void validateUpdateOrderInput(UpdateOrderInput input){
        Map<Product, Integer> productToNewQuantityMap = input.getItems().stream()
                .collect(Collectors.toMap(
                        item -> productRepository.findProductById(item.getProductId())
                                .orElseThrow(() -> new CustomGraphQLException(String.format("Product with id %s not found: ", item.getProductId()), 404)),
                        OrderItemInput::getQuantity
                ));
        if (productToNewQuantityMap.containsValue(null)) {
            throw new CustomGraphQLException("Some products specified do not exist", 400);
        }
        productToNewQuantityMap.forEach((product, newQuantity) -> {
            if (newQuantity <= 0) {
                throw new CustomGraphQLException("Quantity has to be more than 0", 400);
            }
            if (product.getStock() < newQuantity) {
                throw new CustomGraphQLException(String.format(
                        "Product %s does not have enough stock to fulfill the order",
                        product.getName()), 400);
            }
        });
    }

    public Map<Product, Integer> validateCreateOrderInput(CreateOrderInput input){
        Map<Product, Integer> productToNewQuantityMap = input.getItems().stream()
                .collect(Collectors.toMap(
                        item -> productRepository.findProductById(item.getProductId())
                                .orElseThrow(() -> new CustomGraphQLException(String.format("Product with id %s not found: ", item.getProductId()), 404)),
                        OrderItemInput::getQuantity
                ));
        if (productToNewQuantityMap.containsValue(null)) {
            throw new CustomGraphQLException("Some products specified do not exist", 400);
        }
        productToNewQuantityMap.forEach((product, newQuantity) -> {
            if (newQuantity <= 0) {
                throw new CustomGraphQLException("Quantity has to be more than 0", 400);
            }
            if (product.getStock() < newQuantity) {
                throw new CustomGraphQLException(String.format(
                        "Product %s does not have enough stock to fulfill the order",
                        product.getName()), 400);
            }
        });
        return productToNewQuantityMap;
    }
}