package com.example.ordermanagementsystem.config;

import com.example.ordermanagementsystem.Dto.ProductPayload;
import com.example.ordermanagementsystem.Dto.UserPayload;
import com.example.ordermanagementsystem.entity.Product;
import com.example.ordermanagementsystem.entity.User;
import com.example.ordermanagementsystem.payload.UpdateProductInput;
import com.example.ordermanagementsystem.payload.UpdateUserInput;
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
    void updateFields(@MappingTarget User user, UpdateUserInput input);
    void updateFields(@MappingTarget Product product, UpdateProductInput input);
}