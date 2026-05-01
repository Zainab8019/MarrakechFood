package com.example.commandeservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "commande_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommandeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long platId;
    private String nomPlat;
    private Double prixUnitaire;
    private Integer quantite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    @JsonBackReference
    private Commande commande;

    // Méthode setter personnalisée
    public void setCommande(Commande commande) {
        this.commande = commande;
    }
}