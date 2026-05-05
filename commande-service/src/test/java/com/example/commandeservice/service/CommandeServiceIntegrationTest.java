package com.example.commandeservice.service;

import com.example.commandeservice.client.ClientServiceClient;
import com.example.commandeservice.client.RestaurantServiceClient;
import com.example.commandeservice.entity.Commande;
import com.example.commandeservice.entity.CommandeItem;
import com.example.commandeservice.entity.StatutCommande;
import com.example.commandeservice.repository.CommandeRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommandeServiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CommandeRepository commandeRepository;

    @MockBean
    private ClientServiceClient clientServiceClient;

    @MockBean
    private RestaurantServiceClient restaurantServiceClient;

    private static Long createdCommandeId;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.H2Dialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeEach
    void setUp() {
        commandeRepository.deleteAll();
        when(clientServiceClient.existsById(anyLong())).thenReturn(true);
        when(restaurantServiceClient.existsById(anyLong())).thenReturn(true);
        
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("🧪 Début des tests d'intégration Commande Service");
        System.out.println("═══════════════════════════════════════════════════");
    }

    @Test
    @Order(1)
    void test01_CreerCommande() {
        System.out.println("\n📝 TEST 1: Création d'une commande");

        Commande commande = creerCommandeTest();
        
        ResponseEntity<Commande> response = restTemplate.postForEntity(
                "/api/commandes",
                commande,
                Commande.class
        );

        System.out.println("   Status: " + response.getStatusCode());
        System.out.println("   ID créé: " + (response.getBody() != null ? response.getBody().getId() : "null"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();

        createdCommandeId = response.getBody().getId();
        System.out.println("   ✅ TEST 1 RÉUSSI - ID: " + createdCommandeId);
    }

    @Test
    @Order(2)
    void test02_GetCommandeById() {
        System.out.println("\n📝 TEST 2: Récupération commande par ID");

        Commande commande = creerCommandeTest();
        ResponseEntity<Commande> created = restTemplate.postForEntity("/api/commandes", commande, Commande.class);
        Long id = created.getBody().getId();

        ResponseEntity<Commande> response = restTemplate.getForEntity(
                "/api/commandes/" + id,
                Commande.class
        );

        System.out.println("   Status: " + response.getStatusCode());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(id);
        
        System.out.println("   ✅ TEST 2 RÉUSSI");
    }

    @Test
    @Order(3)
    void test03_GetAllCommandes() {
        System.out.println("\n📝 TEST 3: Récupération de toutes les commandes");

        restTemplate.postForEntity("/api/commandes", creerCommandeTest(), Commande.class);
        restTemplate.postForEntity("/api/commandes", creerCommandeTest(), Commande.class);

        ResponseEntity<Commande[]> response = restTemplate.getForEntity(
                "/api/commandes",
                Commande[].class
        );

        System.out.println("   Status: " + response.getStatusCode());
        System.out.println("   Nombre: " + (response.getBody() != null ? response.getBody().length : 0));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(2);
        
        System.out.println("   ✅ TEST 3 RÉUSSI");
    }

    @Test
    @Order(4)
    void test04_GetCommandesByClient() {
        System.out.println("\n📝 TEST 4: Récupération commandes par client");

        Commande cmd1 = creerCommandeTest();
        cmd1.setClientId(1L);
        restTemplate.postForEntity("/api/commandes", cmd1, Commande.class);
        
        Commande cmd2 = creerCommandeTest();
        cmd2.setClientId(2L);
        restTemplate.postForEntity("/api/commandes", cmd2, Commande.class);

        ResponseEntity<Commande[]> response = restTemplate.getForEntity(
                "/api/commandes/client/1",
                Commande[].class
        );

        System.out.println("   Status: " + response.getStatusCode());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(1);
        
        System.out.println("   ✅ TEST 4 RÉUSSI");
    }

    @Test
    @Order(5)
    void test05_ValiderCommande() {
        System.out.println("\n📝 TEST 5: Validation d'une commande");

        Commande commande = creerCommandeTest();
        ResponseEntity<Commande> created = restTemplate.postForEntity("/api/commandes", commande, Commande.class);
        Long id = created.getBody().getId();
        
        System.out.println("   Statut initial: " + created.getBody().getStatut());

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/commandes/" + id + "/valider",
                HttpMethod.PUT,
                null,
                String.class
        );

        System.out.println("   Status validation: " + response.getStatusCode());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        ResponseEntity<Commande> verification = restTemplate.getForEntity("/api/commandes/" + id, Commande.class);
        System.out.println("   Nouveau statut: " + verification.getBody().getStatut());
        
        System.out.println("   ✅ TEST 5 RÉUSSI");
    }

    @Test
    @Order(6)
    void test06_AssignerLivreur() {
        System.out.println("\n📝 TEST 6: Assignation d'un livreur");

        Commande commande = creerCommandeTest();
        ResponseEntity<Commande> created = restTemplate.postForEntity("/api/commandes", commande, Commande.class);
        Long id = created.getBody().getId();
        
        restTemplate.exchange("/api/commandes/" + id + "/valider", HttpMethod.PUT, null, String.class);
        
        System.out.println("   Livreur ID: 10");

        ResponseEntity<Commande> response = restTemplate.exchange(
                "/api/commandes/" + id + "/assigner/10",
                HttpMethod.PUT,
                null,
                Commande.class
        );

        System.out.println("   Status assignation: " + response.getStatusCode());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getLivreurId()).isEqualTo(10L);
        
        System.out.println("   ✅ TEST 6 RÉUSSI");
    }

    @Test
    @Order(7)
    void test07_ClientInexistant() {
        System.out.println("\n📝 TEST 7: Commande avec client inexistant");

        when(clientServiceClient.existsById(999L)).thenReturn(false);

        Commande commande = new Commande();
        commande.setClientId(999L);
        commande.setRestaurantId(1L);
        commande.setAdresseLivraison("Marrakech");
        commande.setTotal(100.0);
        commande.setStatut(StatutCommande.EN_ATTENTE);
        commande.setDateCommande(LocalDateTime.now());

        CommandeItem item = new CommandeItem();
        item.setPlatId(1L);
        item.setNomPlat("Pizza");
        item.setPrixUnitaire(50.0);
        item.setQuantite(2);
        item.setCommande(commande);
        commande.setItems(List.of(item));

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/commandes",
                commande,
                Map.class
        );

        System.out.println("   Status: " + response.getStatusCode());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("error");
        
        System.out.println("   ✅ TEST 7 RÉUSSI");
    }

    @Test
    @Order(8)
    void test08_CommandeNotFound() {
        System.out.println("\n📝 TEST 8: Récupération commande inexistante");

        ResponseEntity<Commande> response = restTemplate.getForEntity(
                "/api/commandes/99999",
                Commande.class
        );

        System.out.println("   Status: " + response.getStatusCode());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        
        System.out.println("   ✅ TEST 8 RÉUSSI");
    }

    // ==================== MÉTHODE UTILITAIRE ====================
    private Commande creerCommandeTest() {
        Commande commande = new Commande();
        commande.setClientId(1L);
        commande.setRestaurantId(1L);
        commande.setAdresseLivraison("Marrakech");
        commande.setTotal(100.0);
        commande.setStatut(StatutCommande.EN_ATTENTE);
        commande.setDateCommande(LocalDateTime.now());

        CommandeItem item = new CommandeItem();
        item.setPlatId(1L);
        item.setNomPlat("Pizza Margherita");
        item.setPrixUnitaire(50.0);
        item.setQuantite(2);
        item.setCommande(commande);

        commande.setItems(List.of(item));
        
        return commande;
    }
}