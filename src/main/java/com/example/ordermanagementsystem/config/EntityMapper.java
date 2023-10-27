package com.example.ordermanagementsystem.config;

import com.example.ordermanagementsystem.Payload.OrderPayload;
import com.example.ordermanagementsystem.Payload.ProductPayload;
import com.example.ordermanagementsystem.Payload.UserPayload;
import com.example.ordermanagementsystem.entity.Order;
import com.example.ordermanagementsystem.entity.Product;
import com.example.ordermanagementsystem.entity.ProductLine;
import com.example.ordermanagementsystem.entity.User;
import com.example.ordermanagementsystem.input.UpdateOrderInput;
import com.example.ordermanagementsystem.input.UpdateProductInput;
import com.example.ordermanagementsystem.input.UpdateUserInput;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component
public interface EntityMapper {
    @Mapping(target = "id", source = "id")
    UserPayload userToUserPayload(User user);
    @Mapping(target = "id", source = "id")
    ProductPayload productToProductPayload(Product product);
    @Mapping(target = "id", source = "id")
    @Mapping(target = "user", source = "users")
    OrderPayload orderToOrderPayload(Order order);
    void updateFields(@MappingTarget User user, UpdateUserInput input);
    void updateFields(@MappingTarget Product product, UpdateProductInput input);
    void updateFields(@MappingTarget Order order, UpdateOrderInput input);
    void updateFields(@MappingTarget ProductLine productLine, ProductLine input);
}