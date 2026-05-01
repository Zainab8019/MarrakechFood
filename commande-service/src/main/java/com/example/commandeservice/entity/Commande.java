package com.example.commandeservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commandes")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clientId;
    private Long restaurantId;
    private Long livreurId;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CommandeItem> items = new ArrayList<>();

    private Double total = 0.0;

    @Enumerated(EnumType.STRING)
    private StatutCommande statut = StatutCommande.EN_ATTENTE;

    private String adresseLivraison;
    private LocalDateTime dateCommande = LocalDateTime.now();
    private LocalDateTime dateLivraison;

    @Column(length = 10000)
    private String qrCodeBase64;

    // ==================== MÉTHODE IMPORTANTE ====================
    public void addItem(CommandeItem item) {
        if (item != null) {
            this.items.add(item);
            // On ne met pas item.setCommande(this) ici pour éviter la boucle
        }
    }
}