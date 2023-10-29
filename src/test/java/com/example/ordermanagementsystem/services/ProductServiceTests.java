package com.example.ordermanagementsystem.services;

import com.example.ordermanagementsystem.config.EntityMapper;
import com.example.ordermanagementsystem.entity.Product;
import com.example.ordermanagementsystem.exception.CustomGraphQLException;
import com.example.ordermanagementsystem.input.*;
import com.example.ordermanagementsystem.payload.ProductPayload;
import com.example.ordermanagementsystem.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private EntityMapper entityMapper;

    private Product product;

    private CreateProductInput createProductInput;
    private UpdateProductInput updateProductInput;
    private ProductPayload productPayload;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    public void init() {
        product = new Product(1L, "Nike", 10,  50D);
        createProductInput = new CreateProductInput("Nike", 10, 50D);
        updateProductInput = new UpdateProductInput(1L, "Nike", 10, 50D);
        productPayload = new ProductPayload(1L, product.getName(), product.getStock(), product.getPrice());
    }
    @Test
    void createProduct_ShouldSucceed() {
        Mockito.when(productRepository.findProductByName(createProductInput.getName())).thenReturn(Optional.empty());
        Mockito.when(productRepository.save(any())).thenReturn(product);
        Mockito.when(entityMapper.productToProductPayload(any())).thenReturn(productPayload);
        var savedProduct = productService.createProduct(createProductInput);
        Assertions.assertThat(savedProduct).isEqualTo
                (productPayload);
    }
    @Test
    void createProduct_ShouldThrowExceptionIfProductAlreadyExists() {
        Mockito.when(productRepository.findProductByName(createProductInput.getName())).thenReturn(Optional.of(product));
        Assertions.assertThatThrownBy(() -> {
                    productService.createProduct(createProductInput);
                }).isInstanceOf(CustomGraphQLException.class)
                .hasMessage(String.format("Product with name %s already exists", product.getName()));
    }

    @Test
    void updateProduct_ShouldSucceed() {
        Mockito.when(productRepository.findProductById(updateProductInput.getId())).thenReturn(Optional.of(product));
        Mockito.when(productRepository.save(any())).thenReturn(product);
        Mockito.when(entityMapper.productToProductPayload(any())).thenReturn(productPayload);
        var savedProduct = productService.updateProduct(updateProductInput);
        Assertions.assertThat(savedProduct).isEqualTo
                (productPayload);
    }

    @Test
    void updateProduct_ShouldThrowExceptionIfProductDoesNotExist() {
        Mockito.when(productRepository.findProductById(updateProductInput.getId())).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> {
                    productService.updateProduct(updateProductInput);
                }).isInstanceOf(CustomGraphQLException.class)
                .hasMessage(String.format("Product with id %s does not exist", product.getId()));
    }
}