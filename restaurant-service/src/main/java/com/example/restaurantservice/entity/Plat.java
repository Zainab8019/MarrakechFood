package com.example.restaurantservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Plat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String description;
    private Double prix;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}