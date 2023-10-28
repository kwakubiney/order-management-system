package com.example.ordermanagementsystem.services;


import com.example.ordermanagementsystem.payload.GenericMessage;
import com.example.ordermanagementsystem.payload.ProductPayload;
import com.example.ordermanagementsystem.config.EntityMapper;
import com.example.ordermanagementsystem.entity.Product;
import com.example.ordermanagementsystem.exception.CustomGraphQLException;
import com.example.ordermanagementsystem.input.CreateProductInput;
import com.example.ordermanagementsystem.input.UpdateProductInput;
import com.example.ordermanagementsystem.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final EntityMapper entityMapper;
    @Secured({"ROLE_ADMIN"})
    @Override
    public ProductPayload createProduct(CreateProductInput input) {
        Optional<Product> existingProduct = productRepository.findProductByName(input.getName());
        if (existingProduct.isPresent()){
            throw new CustomGraphQLException(String.format("Product with name %s already exists", input.getName()), 400);
        }
        Product productToBeSaved = Product.builder().name(input.getName())
                .stock(input.getStock())
                .price(input.getPrice())
                .build();
        Product savedProduct = productRepository.save(productToBeSaved);
        return entityMapper.productToProductPayload(savedProduct);
    }

    @Override
    public ProductPayload product(Long id) {
        Optional<Product> existingProduct = productRepository.findProductById(id);
        if (existingProduct.isEmpty()){
            throw new CustomGraphQLException(String.format("Product with id %s does not exist", id), 404);
        }
        return entityMapper.productToProductPayload(existingProduct.get());
    }

    @Override
    public List<ProductPayload> products() {
        return productRepository.findAll().stream().map(
                (product)-> ProductPayload.builder().
                        id(product.getId())
                        .name(product.getName())
                        .stock(product.getStock())
                        .build()
        ).collect(Collectors.toList());
    }

    @Secured("ROLE_ADMIN")
    @Override
    public ProductPayload updateProduct(UpdateProductInput input){
        Optional<Product> existingProduct = productRepository.findProductById(input.getId());
        if (existingProduct.isEmpty()){
            throw new CustomGraphQLException(String.format("Product with id %s does not exist", input.getId()), 404);
        }
        var x = entityMapper.productInputToProduct(existingProduct.get(), input);
        var updatedProduct = productRepository.save(existingProduct.get());
        return entityMapper.productToProductPayload(updatedProduct);
    }

    @Secured("ROLE_ADMIN")
    @Override
    public GenericMessage deleteProduct(Long id) {
        Optional<Product> existingProduct = productRepository.findProductById(id);
        if (existingProduct.isEmpty()){
            throw new CustomGraphQLException(String.format("Product with id %s does not exist", id), 404);
        }
        productRepository.deleteById(id);
        return new GenericMessage(String.format("Product with id %s has successfully been deleted", id));
    }
}