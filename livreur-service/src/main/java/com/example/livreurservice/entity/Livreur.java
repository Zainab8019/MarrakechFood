package com.example.livreurservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "livreurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Livreur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String telephone;

    private String email;

    @Enumerated(EnumType.STRING)
    private StatutLivreur statut = StatutLivreur.DISPONIBLE;

    private Double latitude;
    private Double longitude;
}