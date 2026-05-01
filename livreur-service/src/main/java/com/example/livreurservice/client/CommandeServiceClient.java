package com.example.livreurservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "commande-service", url = "http://localhost:8084")
public interface CommandeServiceClient {

    @PutMapping("/api/commandes/{commandeId}/assigner/{livreurId}")
    void assignerLivreur(@PathVariable Long commandeId, @PathVariable Long livreurId);

    
    @PutMapping("/api/commandes/{commandeId}/confirmer-livraison")
    void confirmerLivraison(@PathVariable Long commandeId);
}