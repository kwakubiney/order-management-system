package com.example.ordermanagementsystem.services;

import com.example.ordermanagementsystem.Dto.GenericMessage;
import com.example.ordermanagementsystem.Dto.ProductPayload;
import com.example.ordermanagementsystem.payload.CreateProductInput;
import com.example.ordermanagementsystem.payload.UpdateProductInput;


import java.util.List;

public interface ProductService {
    ProductPayload createProduct(CreateProductInput payload);
    ProductPayload product(Long id);
    List<ProductPayload> products();
    ProductPayload updateProduct(UpdateProductInput payload);
    GenericMessage deleteProduct(Long id);
}
