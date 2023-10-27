package com.example.ordermanagementsystem.services;

import com.example.ordermanagementsystem.Payload.GenericMessage;
import com.example.ordermanagementsystem.Payload.ProductPayload;
import com.example.ordermanagementsystem.input.CreateProductInput;
import com.example.ordermanagementsystem.input.UpdateProductInput;


import java.util.List;

public interface ProductService {
    ProductPayload createProduct(CreateProductInput payload);
    ProductPayload product(Long id);
    List<ProductPayload> products();
    ProductPayload updateProduct(UpdateProductInput payload);
    GenericMessage deleteProduct(Long id);
}
