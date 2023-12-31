package com.example.ordermanagementsystem.services;

import com.example.ordermanagementsystem.payload.GenericMessage;
import com.example.ordermanagementsystem.payload.ProductPayload;
import com.example.ordermanagementsystem.input.CreateProductInput;
import com.example.ordermanagementsystem.input.UpdateProductInput;
import org.springframework.security.core.Authentication;


import java.util.List;

public interface ProductService {
    ProductPayload createProduct(CreateProductInput payload);
    ProductPayload product(Long id);
    List<ProductPayload> products();
    ProductPayload updateProduct(UpdateProductInput payload);
    GenericMessage deleteProduct(Long id);
}
