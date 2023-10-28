package com.example.ordermanagementsystem.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductLine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Product product;
    @Column(nullable = false)
    private int quantity;
    @JoinColumn(name="order_id")
    @ManyToOne
    private Order order;
}