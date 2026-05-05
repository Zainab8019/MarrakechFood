package com.example.commandeservice.service;
import com.example.commandeservice.entity.CommandeItem;
import com.example.commandeservice.client.ClientServiceClient;
import com.example.commandeservice.client.RestaurantServiceClient;
import com.example.commandeservice.entity.Commande;
import com.example.commandeservice.entity.StatutCommande;
import com.example.commandeservice.repository.CommandeRepository;
import com.example.commandeservice.util.QRCodeGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommandeService {

    private final CommandeRepository repository;
    private final ClientServiceClient clientServiceClient;
    private final RestaurantServiceClient restaurantServiceClient;

    public CommandeService(CommandeRepository repository,
                           ClientServiceClient clientServiceClient,
                           RestaurantServiceClient restaurantServiceClient) {
        this.repository = repository;
        this.clientServiceClient = clientServiceClient;
        this.restaurantServiceClient = restaurantServiceClient;
    }

    public Commande createCommande(Commande commande) {

        System.out.println("CLIENT ID = " + commande.getClientId());
        System.out.println("RESTO ID = " + commande.getRestaurantId());
        System.out.println("ITEMS = " + commande.getItems());

        if (commande.getItems() == null || commande.getItems().isEmpty()) {
            throw new RuntimeException("Items obligatoires");
        }

       if (!clientServiceClient.existsById(commande.getClientId())) {
            throw new RuntimeException("Client invalide");
        }

        if (!restaurantServiceClient.existsById(commande.getRestaurantId())) {
            throw new RuntimeException("Restaurant invalide");
        }

        for (CommandeItem item : commande.getItems()) {
            item.setCommande(commande);
        }
        try {
            boolean clientExists = clientServiceClient.existsById(commande.getClientId());
            System.out.println("CLIENT EXISTS = " + clientExists);

            boolean restoExists = restaurantServiceClient.existsById(commande.getRestaurantId());
            System.out.println("RESTO EXISTS = " + restoExists);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur communication microservices");
        }

        double total = commande.getItems().stream()
                .mapToDouble(i -> i.getPrixUnitaire() * i.getQuantite())
                .sum();

        commande.setTotal(total);
        commande.setStatut(StatutCommande.EN_ATTENTE);
        commande.setDateCommande(LocalDateTime.now());

        return repository.save(commande);
    }

    @Transactional
    public Commande validerCommande(Long id) {

        Commande commande = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        if (commande.getStatut() != StatutCommande.EN_ATTENTE) {
            throw new RuntimeException("La commande ne peut pas être validée");
        }

        //  FIX RELATION JPA (IMPORTANT)
      /*  if (commande.getItems() != null) {
            for (CommandeItem item : commande.getItems()) {
                item.setCommande(commande);
            }
        }*/

        //  AJOUT QR CODE SÉCURISÉ
        try {
            String qrData = "COMMANDE_" + commande.getId();
            String qrBase64 = QRCodeGenerator.generateQRCode(qrData);
            commande.setQrCodeBase64(qrBase64);
        } catch (Exception e) {
            // ⚠️ IMPORTANT : ne pas casser l'API
            System.out.println("Erreur QR Code : " + e.getMessage());
        }
        

        commande.setStatut(StatutCommande.VALIDEE);

        return repository.save(commande);
        
    }
    public List<Commande> getAllCommandes() {
        return repository.findAll();
    }

    public Commande getCommandeById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
    }

    public List<Commande> getCommandesByClient(Long clientId) {
        return repository.findByClientId(clientId);
    }
    @Transactional
    public Commande assignerLivreur(Long commandeId, Long livreurId) {
        Commande commande = repository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        if (commande.getStatut() != StatutCommande.VALIDEE) {
            throw new RuntimeException("La commande doit être validée avant d'assigner un livreur");
        }

        commande.setLivreurId(livreurId);
        commande.setStatut(StatutCommande.EN_LIVRAISON);

        return repository.save(commande);
    }
    @Transactional
    public Commande confirmerLivraison(Long commandeId) {
        Commande commande = repository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        if (commande.getStatut() != StatutCommande.EN_LIVRAISON) {
            throw new RuntimeException("La commande n'est pas en cours de livraison");
        }

        commande.setStatut(StatutCommande.LIVREE);
        commande.setDateLivraison(LocalDateTime.now());

        return repository.save(commande);
    }
}