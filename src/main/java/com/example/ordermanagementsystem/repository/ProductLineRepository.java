package com.example.ordermanagementsystem.repository;

import com.example.ordermanagementsystem.entity.ProductLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductLineRepository extends JpaRepository<ProductLine, Long> {
}