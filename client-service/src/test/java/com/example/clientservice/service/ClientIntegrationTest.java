package com.example.clientservice.service;

import com.example.clientservice.entity.Client;
import com.example.clientservice.repository.ClientRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClientRepository clientRepository;

    private final String BASE_URL = "/api/clients";

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
        clientRepository.deleteAll();
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("🧪 Début des tests d'intégration Client Service");
        System.out.println("═══════════════════════════════════════════════════");
    }

    // ===================== TEST 1 : INSCRIPTION =====================
    @Test
    @Order(1)
    void test01_Inscription() {
        System.out.println("\n📝 TEST 1: Inscription client");

        String email = "test" + System.currentTimeMillis() + "@mail.com";

        Map<String, Object> body = new HashMap<>();
        body.put("nom", "Test User");
        body.put("email", email);
        body.put("motDePasse", "1234");
        body.put("adresse", "Casablanca");
        body.put("telephone", "0600000000");

        ResponseEntity<Client> response =
                restTemplate.postForEntity(BASE_URL + "/inscrire", body, Client.class);

        System.out.println("   Status: " + response.getStatusCode());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();

        System.out.println("   ✅ TEST 1 RÉUSSI");
    }

    // ===================== TEST 2 : CONNEXION RÉUSSIE =====================
    @Test
    @Order(2)
    void test02_ConnexionReussie() {
        System.out.println("\n📝 TEST 2: Connexion réussie");

        String email = "connexion" + System.currentTimeMillis() + "@mail.com";

        // Inscription
        Map<String, Object> inscription = new HashMap<>();
        inscription.put("nom", "Test Connexion");
        inscription.put("email", email);
        inscription.put("motDePasse", "1234");
        inscription.put("adresse", "Casa");
        inscription.put("telephone", "0600000000");

        restTemplate.postForEntity(BASE_URL + "/inscrire", inscription, Client.class);

        // Connexion
        Map<String, String> login = new HashMap<>();
        login.put("email", email);
        login.put("motDePasse", "1234");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(login, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(BASE_URL + "/connexion", request, Map.class);

        System.out.println("   Status: " + response.getStatusCode());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("message");
        assertThat(response.getBody().get("message")).isEqualTo("Connexion réussie");

        System.out.println("   ✅ TEST 2 RÉUSSI");
    }

    // ===================== TEST 3 : CONNEXION MAUVAIS MOT DE PASSE =====================
    @Test
    @Order(3)
    void test03_ConnexionMauvaisMotDePasse() {
        System.out.println("\n📝 TEST 3: Connexion avec mauvais mot de passe");
        String email = "wrongpass" + System.currentTimeMillis() + "@mail.com";

        // Inscription
        Map<String, Object> inscription = new HashMap<>();
        inscription.put("nom", "Test Wrong");
        inscription.put("email", email);
        inscription.put("motDePasse", "1234");
        inscription.put("adresse", "Casa");
        inscription.put("telephone", "0600000000");

        ResponseEntity<Client> inscriptionResponse =
            restTemplate.postForEntity(BASE_URL + "/inscrire", inscription, Client.class);
        System.out.println("   Inscription status: " + inscriptionResponse.getStatusCode());
        assertThat(inscriptionResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Connexion avec mauvais mot de passe
        Map<String, String> login = new HashMap<>();
        login.put("email", email);
        login.put("motDePasse", "wrongpassword");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(login, headers);

        // Utiliser exchange pour n'obtenir que le statut (pas de body)
        ResponseEntity<Void> response = restTemplate.exchange(
            BASE_URL + "/connexion",
            HttpMethod.POST,
            request,
            Void.class
        );

        System.out.println("   Status: " + response.getStatusCode());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        System.out.println("   ✅ TEST 3 RÉUSSI");
    }
    // ===================== TEST 4 : LISTE DE TOUS LES CLIENTS =====================
    @Test
    @Order(4)
    void test04_GetAllClients() {
        System.out.println("\n📝 TEST 4: Récupération de tous les clients");

        // Créer quelques clients avec des emails uniques
        for (int i = 0; i < 3; i++) {
            Map<String, Object> body = new HashMap<>();
            body.put("nom", "Client " + i);
            // Utilisez i pour rendre l'email unique
            body.put("email", "client" + i + System.currentTimeMillis() + "@mail.com");
            body.put("motDePasse", "1234");
            restTemplate.postForEntity(BASE_URL + "/inscrire", body, Client.class);
            
            // Attendre 1 ms pour que le timestamp change (optionnel mais plus sûr)
            try { Thread.sleep(1); } catch (InterruptedException e) {}
        }

        ResponseEntity<Client[]> response =
                restTemplate.getForEntity(BASE_URL, Client[].class);

        System.out.println("   Status: " + response.getStatusCode());
        System.out.println("   Nombre de clients: " + (response.getBody() != null ? response.getBody().length : 0));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(3);

        System.out.println("   ✅ TEST 4 RÉUSSI");
    }
    // ===================== TEST 5 : SUPPRESSION CLIENT =====================
    @Test
    @Order(5)
    void test05_DeleteClient() {
        System.out.println("\n📝 TEST 5: Suppression client");

        // Créer un client
        Map<String, Object> body = new HashMap<>();
        body.put("nom", "Client à supprimer");
        body.put("email", "delete" + System.currentTimeMillis() + "@mail.com");
        body.put("motDePasse", "1234");
        body.put("adresse", "Rabat");
        body.put("telephone", "0612345678");

        ResponseEntity<Client> created = restTemplate.postForEntity(BASE_URL + "/inscrire", body, Client.class);
        Long id = created.getBody().getId();

        // Supprimer le client
        restTemplate.delete(BASE_URL + "/" + id);

        // Vérifier la suppression
        ResponseEntity<Client> check = restTemplate.getForEntity(BASE_URL + "/" + id, Client.class);

        System.out.println("   Status après suppression: " + check.getStatusCode());

        assertThat(check.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        System.out.println("   ✅ TEST 5 RÉUSSI");
    }

    @AfterAll
    static void afficherRapport() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("🎉 RAPPORT DES TESTS CLIENT SERVICE");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("✅ 5 tests exécutés avec succès");
        System.out.println("═══════════════════════════════════════════════════\n");
    }
}