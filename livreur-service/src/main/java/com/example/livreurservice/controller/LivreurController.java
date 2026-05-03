package com.example.livreurservice.controller;

import com.example.livreurservice.entity.Livreur;
import com.example.livreurservice.entity.StatutLivreur;
import com.example.livreurservice.service.LivreurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livreurs")
@CrossOrigin(origins = "*")
public class LivreurController {

    private final LivreurService livreurService;

    public LivreurController(LivreurService livreurService) {
        this.livreurService = livreurService;
    }

    @GetMapping("/test")
    public String test() {
        return "✅ Service Livreur fonctionne sur le port 8085 !";
    }

    @PostMapping
    public ResponseEntity<Livreur> ajouterLivreur(@RequestBody Livreur livreur) {
        return ResponseEntity.ok(livreurService.ajouterLivreur(livreur));
    }

    @GetMapping
    public List<Livreur> getAll() {
        return livreurService.getAllLivreurs();
    }

    @GetMapping("/disponibles")
    public List<Livreur> getDisponibles() {
        return livreurService.getLivreursDisponibles();
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<String> changerStatut(@PathVariable Long id, @RequestParam StatutLivreur statut) {
        livreurService.changerStatut(id, statut);
        return ResponseEntity.ok("Statut mis à jour");
    }
    @PutMapping("/assigner/{commandeId}")
    public ResponseEntity<Livreur> assignerALaCommande(@PathVariable Long commandeId) {
        return ResponseEntity.ok(livreurService.assignerALaCommande(commandeId));
    }
    @PostMapping("/scan-qr")
    public ResponseEntity<String> scannerQR(@RequestBody String qrCodeData) {
        String message = livreurService.scannerQRCode(qrCodeData);
        return ResponseEntity.ok(message);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Livreur> getById(@PathVariable Long id) {
        return livreurService.getLivreurById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    //update
    
    @PutMapping("/{id}")
    public ResponseEntity<Livreur> updateLivreur(
            @PathVariable Long id,
            @RequestBody Livreur livreur) {

        return ResponseEntity.ok(livreurService.updateLivreur(id, livreur));
    }
    //suppression
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLivreur(@PathVariable Long id) {
        livreurService.deleteLivreur(id);
        return ResponseEntity.ok("Livreur supprimé");
    }
    
    
}