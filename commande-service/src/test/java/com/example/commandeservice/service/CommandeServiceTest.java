package com.example.commandeservice.service;

import com.example.commandeservice.client.ClientServiceClient;
import com.example.commandeservice.client.RestaurantServiceClient;
import com.example.commandeservice.entity.*;
import com.example.commandeservice.repository.CommandeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandeServiceTest {

    private CommandeRepository repository;
    private ClientServiceClient clientService;
    private RestaurantServiceClient restaurantService;
    private CommandeService commandeService;

    @BeforeEach
    void setUp() {
        repository = mock(CommandeRepository.class);
        clientService = mock(ClientServiceClient.class);
        restaurantService = mock(RestaurantServiceClient.class);

        commandeService = new CommandeService(repository, clientService, restaurantService);
    }

    // ================= CREATE SUCCESS =================
    @Test
    void createCommande_success() {

        when(clientService.existsById(2L)).thenReturn(true);
        when(restaurantService.existsById(1L)).thenReturn(true);

        Commande cmd = new Commande();
        cmd.setClientId(2L);
        cmd.setRestaurantId(1L);

        CommandeItem item = new CommandeItem();
        item.setPrixUnitaire(50.0);
        item.setQuantite(2);

        cmd.setItems(List.of(item));

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        Commande result = commandeService.createCommande(cmd);

        assertNotNull(result);
        assertEquals(100.0, result.getTotal());
        assertEquals(StatutCommande.EN_ATTENTE, result.getStatut());
    }

    // ================= CLIENT FAIL =================
    @Test
    void createCommande_clientNotFound() {

        when(clientService.existsById(2L)).thenReturn(false);

        Commande cmd = new Commande();
        cmd.setClientId(2L);
        cmd.setRestaurantId(1L);
        cmd.setItems(List.of(new CommandeItem()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> commandeService.createCommande(cmd));

        assertTrue(ex.getMessage().contains("Client"));
    }

    // ================= RESTAURANT FAIL =================
    @Test
    void createCommande_restaurantNotFound() {

        when(clientService.existsById(2L)).thenReturn(true);
        when(restaurantService.existsById(1L)).thenReturn(false);

        Commande cmd = new Commande();
        cmd.setClientId(2L);
        cmd.setRestaurantId(1L);
        cmd.setItems(List.of(new CommandeItem()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> commandeService.createCommande(cmd));

        assertTrue(ex.getMessage().contains("Restaurant"));
    }

    // ================= ITEMS EMPTY =================
    @Test
    void createCommande_itemsEmpty() {

        Commande cmd = new Commande();
        cmd.setClientId(2L);
        cmd.setRestaurantId(1L);
        cmd.setItems(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> commandeService.createCommande(cmd));

        assertTrue(ex.getMessage().contains("Items"));
    }

    // ================= VALIDER SUCCESS =================
    @Test
    void validerCommande_success() {

        Commande cmd = new Commande();
        cmd.setId(1L);
        cmd.setStatut(StatutCommande.EN_ATTENTE);
        cmd.setItems(List.of(new CommandeItem()));

        when(repository.findById(1L)).thenReturn(Optional.of(cmd));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        Commande result = commandeService.validerCommande(1L);

        assertEquals(StatutCommande.VALIDEE, result.getStatut());
    }

    // ================= VALIDER FAIL =================
    @Test
    void validerCommande_fail() {

        Commande cmd = new Commande();
        cmd.setId(1L);
        cmd.setStatut(StatutCommande.VALIDEE);

        when(repository.findById(1L)).thenReturn(Optional.of(cmd));

        assertThrows(RuntimeException.class,
                () -> commandeService.validerCommande(1L));
    }

    // ================= ASSIGN SUCCESS =================
    @Test
    void assignerLivreur_success() {

        Commande cmd = new Commande();
        cmd.setId(1L);
        cmd.setStatut(StatutCommande.VALIDEE);

        when(repository.findById(1L)).thenReturn(Optional.of(cmd));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        Commande result = commandeService.assignerLivreur(1L, 10L);

        assertEquals(StatutCommande.EN_LIVRAISON, result.getStatut());
        assertEquals(10L, result.getLivreurId());
    }

    // ================= ASSIGN FAIL =================
    @Test
    void assignerLivreur_fail() {

        Commande cmd = new Commande();
        cmd.setId(1L);
        cmd.setStatut(StatutCommande.EN_ATTENTE);

        when(repository.findById(1L)).thenReturn(Optional.of(cmd));

        assertThrows(RuntimeException.class,
                () -> commandeService.assignerLivreur(1L, 10L));
    }

    // ================= CONFIRM SUCCESS =================
    @Test
    void confirmerLivraison_success() {

        Commande cmd = new Commande();
        cmd.setId(1L);
        cmd.setStatut(StatutCommande.EN_LIVRAISON);

        when(repository.findById(1L)).thenReturn(Optional.of(cmd));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        Commande result = commandeService.confirmerLivraison(1L);

        assertEquals(StatutCommande.LIVREE, result.getStatut());
        assertNotNull(result.getDateLivraison());
    }

    // ================= CONFIRM FAIL =================
    @Test
    void confirmerLivraison_fail() {

        Commande cmd = new Commande();
        cmd.setId(1L);
        cmd.setStatut(StatutCommande.VALIDEE);

        when(repository.findById(1L)).thenReturn(Optional.of(cmd));

        assertThrows(RuntimeException.class,
                () -> commandeService.confirmerLivraison(1L));
    }
}