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

    // ================= CREATE =================
    public Commande createCommande(Commande commande) {

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

        double total = commande.getItems().stream()
                .mapToDouble(i -> i.getPrixUnitaire() * i.getQuantite())
                .sum();

        commande.setTotal(total);
        commande.setStatut(StatutCommande.EN_ATTENTE);
        commande.setDateCommande(LocalDateTime.now());

        return repository.save(commande);
    }

    // ================= VALIDER =================
    @Transactional
    public Commande validerCommande(Long id) {

        Commande c = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        c.setStatut(StatutCommande.VALIDEE);

        c = repository.save(c); // IMPORTANT pour générer ID

        try {
            String qrData = "COMMANDE_" + c.getId();
            String qrBase64 = QRCodeGenerator.generateQRCode(qrData);
            c.setQrCodeBase64(qrBase64);
        } catch (Exception e) {
            System.out.println("QR error: " + e.getMessage());
        }

        return repository.save(c);
    }

    // ================= ASSIGNER LIVREUR =================
    @Transactional
    public Commande assignerLivreur(Long commandeId, Long livreurId) {

        Commande c = repository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        c.setLivreurId(livreurId);
        c.setStatut(StatutCommande.EN_LIVRAISON);

        return repository.save(c);
    }

    // ================= CONFIRMER LIVRAISON =================
    @Transactional
    public Commande confirmerLivraison(Long commandeId) {

        Commande c = repository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        c.setStatut(StatutCommande.LIVREE);
        c.setDateLivraison(LocalDateTime.now());

        return repository.save(c);
    }

    // ================= GET =================
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

    public List<Commande> getCommandesByStatut(StatutCommande statut) {
        return repository.findByStatut(statut);
    }
}