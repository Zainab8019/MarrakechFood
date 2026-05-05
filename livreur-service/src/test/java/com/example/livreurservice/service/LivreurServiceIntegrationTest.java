package com.example.livreurservice.service;

import com.example.livreurservice.entity.Livreur;
import com.example.livreurservice.entity.StatutLivreur;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// Suppression de @Import(TestSecurityConfig.class)
class LivreurServiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static Long id;

    // =========================
    // 1. TEST ENDPOINT
    // =========================
    @Test
    @Order(1)
    void testEndpoint() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/api/livreurs/test", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Service Livreur");
    }

    // =========================
    // 2. CREATE LIVREUR
    // =========================
    @Test
    @Order(2)
    void testCreate() {
        Livreur l = new Livreur();
        l.setNom("Ali");
        l.setTelephone("0612345678");
        l.setEmail("ali@test.com");

        ResponseEntity<Livreur> response =
                restTemplate.postForEntity("/api/livreurs", l, Livreur.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        id = response.getBody().getId();
        assertThat(id).isNotNull();
        assertThat(response.getBody().getStatut()).isEqualTo(StatutLivreur.DISPONIBLE);
    }

    // =========================
    // 3. GET BY ID
    // =========================
    @Test
    @Order(3)
    void testGetById() {
        ResponseEntity<Livreur> response =
                restTemplate.getForEntity("/api/livreurs/" + id, Livreur.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(id);
    }

    // =========================
    // 4. GET ALL
    // =========================
    @Test
    @Order(4)
    void testGetAll() {
        ResponseEntity<Livreur[]> response =
                restTemplate.getForEntity("/api/livreurs", Livreur[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    // =========================
    // 5. DISPONIBLES
    // =========================
    @Test
    @Order(5)
    void testDisponibles() {
        ResponseEntity<Livreur[]> response =
                restTemplate.getForEntity("/api/livreurs/disponibles", Livreur[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // =========================
    // 6. CHANGER STATUT
    // =========================
    @Test
    @Order(6)
    void testChangerStatut() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/livreurs/" + id + "/statut?statut=OCCUPE",
                HttpMethod.PUT,
                null,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Statut mis à jour");
    }

    // =========================
    // 7. UPDATE
    // =========================
    @Test
    @Order(7)
    void testUpdate() {
        Livreur l = new Livreur();
        l.setNom("Updated");
        l.setTelephone("0698765432");
        l.setEmail("updated@test.com");

        ResponseEntity<Livreur> response = restTemplate.exchange(
                "/api/livreurs/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(l),
                Livreur.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNom()).isEqualTo("Updated");
    }

    // =========================
    // 8. DELETE
    // =========================
    @Test
    @Order(8)
    void testDelete() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/livreurs/" + id,
                HttpMethod.DELETE,
                null,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}