package com.example.ordermanagementsystem.controller;

import com.example.ordermanagementsystem.Dto.GenericMessage;
import com.example.ordermanagementsystem.Dto.ProductPayload;
import com.example.ordermanagementsystem.input.CreateProductInput;
import com.example.ordermanagementsystem.input.UpdateProductInput;
import com.example.ordermanagementsystem.services.ProductService;
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
public class ProductController{

    private final ProductService productService;

    @MutationMapping
    public ProductPayload createProduct(@Argument(name = "input") CreateProductInput input) {
        return productService.createProduct(input);
    }

    @MutationMapping
    public ProductPayload updateProduct(@Argument(name = "input") UpdateProductInput input) {
        return productService.updateProduct(input);
    }

    @MutationMapping
    public GenericMessage deleteProduct(@Argument(name = "id") Long id) {
        return productService.deleteProduct(id);
    }

    @QueryMapping
    public ProductPayload product(@Argument(name = "id") Long id) {
        return productService.product(id);
    }

    @QueryMapping
    public List<ProductPayload> products() {
        return productService.products();
    }
}