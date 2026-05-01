package com.example.commandeservice.repository;

import com.example.commandeservice.entity.Commande;
import com.example.commandeservice.entity.StatutCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
    List<Commande> findByClientId(Long clientId);
    List<Commande> findByRestaurantId(Long restaurantId);
    List<Commande> findByLivreurId(Long livreurId);
    List<Commande> findByStatut(StatutCommande statut);
}