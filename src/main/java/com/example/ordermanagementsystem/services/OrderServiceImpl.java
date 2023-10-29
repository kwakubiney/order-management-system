package com.example.ordermanagementsystem.services;

import com.example.ordermanagementsystem.payload.GenericMessage;
import com.example.ordermanagementsystem.payload.OrderPayload;
import com.example.ordermanagementsystem.payload.ProductPayload;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    @Secured("IS_AUTHENTICATED_FULLY")
    @Override
    public OrderPayload createOrder(CreateOrderInput input, Authentication authentication) {
        String emailFromToken = authentication.getName();
        Optional<User> existingUser = userRepository.findUserByEmail(emailFromToken);
        //Check to see if user is allowed to create this order
        if (existingUser.isEmpty()){
            throw new CustomGraphQLException(String.format("User with id %s does not exist", input.getUserId()), 404);
        }
        if (!existingUser.get().getId().equals(input.getUserId())){
            throw new CustomGraphQLException("User is not authorized to create this order", 401);
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
            productService.updateProduct(UpdateProductInput.builder().id(product.getId())
                    .stock(product.getStock()-quantity)
                    .price(product.getPrice())
                    .name(product.getName())
                    .build());
        });
        var savedProductLines = productLineRepository.saveAll(productLines);
        savedOrder.setProducts(savedProductLines);
        return entityMapper.orderToOrderPayload(savedOrder);
    }

    @Secured("IS_AUTHENTICATED_FULLY")
    @Override
    public OrderPayload updateOrder(UpdateOrderInput input, Authentication authentication) {
        String emailFromToken = authentication.getName();
        Optional<User> existingUser = userRepository.findUserByEmail(emailFromToken);
        //Check to see if user is allowed to update this order
        if (!existingUser.get().getId().equals(input.getUserId())){
            throw new CustomGraphQLException("User is not authorized to update this order", 401);
        }

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

        validateUpdateOrderInput(input, existingProductLine);

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

    @Secured("IS_AUTHENTICATED_FULLY")
    @Override
    public GenericMessage deleteOrder(Long id) {
        String emailFromToken = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> existingUser = userRepository.findUserByEmail(emailFromToken);

        Optional<Order> existingOrder = orderRepository.findOrderById(id);
        if (existingOrder.isEmpty()){
            throw new CustomGraphQLException(String.format("Order with id %s does not exist", id), 404);
        }

        //Check to see if user is allowed to delete this order
        if (!existingUser.get().getId().equals(existingOrder.get().getUsers().getId())){
            throw new CustomGraphQLException("User is not authorized to delete this order", 401);
        }

        Map<Product, Integer> existingProductOrderToQuantity = getExistingProductOrderToQuantity(id, existingOrder);
        existingProductOrderToQuantity.forEach(
                (product, quantity) -> {
                    product.setStock(product.getStock() + quantity);
                    productRepository.save(product);
                }
        );
        orderRepository.deleteById(id);
        return new GenericMessage(String.format("Order with id %s has successfully been deleted", id));
    }

    private static Map<Product, Integer> getExistingProductOrderToQuantity(Long id, Optional<Order> existingOrder) {
        if (existingOrder.isEmpty()){
            throw new CustomGraphQLException(String.format("Order with id %s does not exist", id), 404);
        }
        List<ProductLine> existingProductLine = existingOrder.get().getProducts();
        Map<Product, Integer> existingProductOrderToQuantity = new HashMap<>();
        existingProductLine.forEach(
                (element) -> {
                    existingProductOrderToQuantity.put(element.getProduct(), element.getQuantity());
                }
        );
        return existingProductOrderToQuantity;
    }

    @Secured("IS_AUTHENTICATED_FULLY")
    @Override
    public OrderPayload order(Long id) {
        String emailFromToken = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> existingUser = userRepository.findUserByEmail(emailFromToken);
        Optional<Order> existingOrder = orderRepository.findOrderById(id);
        if (existingOrder.isEmpty()){
            throw new CustomGraphQLException(String.format("Order with id %s does not exist", id), 404);
        }
        //Check to see if user is allowed to view this order
        if (!existingUser.get().getId().equals(existingOrder.get().getUsers().getId())){
            throw new CustomGraphQLException("User is not authorized to view this order", 401);
        }
        return entityMapper.orderToOrderPayload(existingOrder.get());
    }

    @Secured("ROLE_ADMIN")
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

    @Secured("IS_AUTHENTICATED_FULLY")
    @Override
    public List<OrderPayload> ordersByUserId(Long id) {
        String emailFromToken = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> existingUser = userRepository.findUserByEmail(emailFromToken);
        if (existingUser.isEmpty()){
            throw new CustomGraphQLException(String.format("User with id %s does not exist", id), 404);
        }
        //Check to see if user is allowed to view this order
        if (!existingUser.get().getId().equals(id)){
            throw new CustomGraphQLException("User is not authorized to view this order", 401);
        }
        return orderRepository.findByUsers_Id(id).stream().map(
                (order)-> OrderPayload.builder().
                        id(order.getId())
                        .products(order.getProducts())
                        .user(order.getUsers())
                        .build()
        ).collect(Collectors.toList());
    }

    @Secured("IS_AUTHENTICATED_FULLY")
    @Override
    public List<ProductPayload> productsByOrderId(Long id) {
        String emailFromToken = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> existingUser = userRepository.findUserByEmail(emailFromToken);

        List<Product> products = new ArrayList<>();
        var existingOrder = orderRepository.findOrderById(id);
        if (existingOrder.isEmpty()){
            throw new CustomGraphQLException(String.format("Order with id %s does not exist", id), 404);
        }

        //Check to see if user is allowed to view this order
        if (!existingUser.get().getId().equals(existingOrder.get().getUsers().getId())){
            throw new CustomGraphQLException("User is not authorized to view this order", 401);
        }

        List<ProductLine> orderProducts = existingOrder.get().getProducts();
        for (ProductLine product : orderProducts) {
            products.add(product.getProduct());
        }
        return products.stream().map(
            entityMapper::productToProductPayload
    ).collect(Collectors.toList());
    }

    public void validateUpdateOrderInput(UpdateOrderInput input, List<ProductLine> productLine){
        Map<Long, Integer> existingProductToQuantity = new HashMap<>();

        productLine.forEach(
                (element) -> {
                    existingProductToQuantity.put(element.getProduct().getId(), element.getQuantity());
                }
        );

        Map<Product, Integer> productToNewQuantityMap;
        //Check to see if any new items exist
        productToNewQuantityMap = input.getItems().stream()
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
            //check if the product id is in existing product to quantity map
            if (existingProductToQuantity.containsKey(product.getId())) {
                if (existingProductToQuantity.get(product.getId()) - newQuantity < 0) {
                    if ((newQuantity - existingProductToQuantity.get(product.getId()) - product.getStock() > 0)) {
                        throw new CustomGraphQLException(String.format(
                                "Product %s does not have enough stock to fulfill the order",
                                product.getName()), 400);
                    }
                }
            }else{
                //handling new orders that did not exist prior
                if (productToNewQuantityMap.containsKey(product)) {
                    if (newQuantity > product.getStock()) {
                            throw new CustomGraphQLException(String.format(
                                    "Product %s does not have enough stock to fulfill the order",
                                    product.getName()), 400);
                        }
                    }
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