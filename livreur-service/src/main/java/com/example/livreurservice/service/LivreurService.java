package com.example.livreurservice.service;

import com.example.livreurservice.client.CommandeServiceClient;
import com.example.livreurservice.entity.Livreur;
import com.example.livreurservice.entity.StatutLivreur;
import com.example.livreurservice.repository.LivreurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.livreurservice.exception.LivreurNotAvailableException;

import java.util.List;
import java.util.Optional;

@Service
public class LivreurService {

    private final LivreurRepository repository;
    private final CommandeServiceClient commandeServiceClient;

    public LivreurService(LivreurRepository repository, CommandeServiceClient commandeServiceClient) {
        this.repository = repository;
        this.commandeServiceClient = commandeServiceClient;
    }

    public Livreur ajouterLivreur(Livreur livreur) {
        if (livreur == null) {
            throw new IllegalArgumentException("Livreur ne peut pas être null");
        }

        livreur.setStatut(StatutLivreur.DISPONIBLE);
        return repository.save(livreur);
    }

    public List<Livreur> getAllLivreurs() {
        return repository.findAll();
    }

    public List<Livreur> getLivreursDisponibles() {
        return repository.findByStatut(StatutLivreur.DISPONIBLE);
    }

    @Transactional
    public Livreur assignerALaCommande(Long commandeId) {
        List<Livreur> disponibles = getLivreursDisponibles();

        if (disponibles.isEmpty()) {
        	throw new LivreurNotAvailableException("Aucun livreur disponible pour le moment");        }

        Livreur livreur = disponibles.get(0);
        livreur.setStatut(StatutLivreur.OCCUPE);

        // Notifier le commande-service
        commandeServiceClient.assignerLivreur(commandeId, livreur.getId());

        return repository.save(livreur);
    }

    public void changerStatut(Long id, StatutLivreur statut) {
        Livreur livreur = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livreur non trouvé"));
        livreur.setStatut(statut);
        repository.save(livreur);
    }
   
    @Transactional
    public String scannerQRCode(String qrCodeData) {
        try {
            if (!qrCodeData.startsWith("COMMANDE_")) {
                throw new RuntimeException("Format de QR Code invalide");
            }

            String[] parts = qrCodeData.split("_");
            Long commandeId = Long.parseLong(parts[1]);
            
            commandeServiceClient.confirmerLivraison(commandeId);
            return "Livraison confirmée avec succès pour la commande N° " + commandeId;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du scan du QR Code : " + e.getMessage());
        }
    }
    public Optional<Livreur> getLivreurById(Long id) {
        return repository.findById(id);
    }
    
    public void deleteLivreur(Long id) {
        repository.deleteById(id);
    }
    public Livreur updateLivreur(Long id, Livreur updated) {
        Livreur l = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livreur non trouvé"));

        l.setNom(updated.getNom());
        l.setTelephone(updated.getTelephone());
        l.setEmail(updated.getEmail());
        l.setLatitude(updated.getLatitude());
        l.setLongitude(updated.getLongitude());

        return repository.save(l);
    }
    
}