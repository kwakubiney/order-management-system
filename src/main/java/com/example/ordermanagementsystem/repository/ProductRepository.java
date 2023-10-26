package com.example.ordermanagementsystem.repository;

import com.example.ordermanagementsystem.entity.Product;
import com.example.ordermanagementsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
    public interface ProductRepository extends JpaRepository<Product, Long> {
        Optional<Product> findProductById(Long id);
        Optional<Product> findProductByName(String name);
    }
