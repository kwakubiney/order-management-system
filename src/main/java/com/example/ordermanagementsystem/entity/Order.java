package com.example.ordermanagementsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders")
public class Order{
    @Id
    private Long id;
    @OneToMany
    private List<ProductLine> products;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user_id;
}