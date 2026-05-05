package com.example.restaurantservice.service;

import com.example.restaurantservice.entity.Plat;
import com.example.restaurantservice.entity.Restaurant;
import com.example.restaurantservice.entity.StatutRestaurant;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.*; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_UPPER=false",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.show-sql=true"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RestaurantServiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static Long createdRestaurantId;
    private static Long createdPlatId;

    // ========================= TEST 1 : Création d'un restaurant =========================
    @Test
    @Order(1)
    void test01_CreateRestaurant() {
        Restaurant r = new Restaurant();
        r.setNom("Restaurant Test");
        r.setAdresse("123 Test Street");
        r.setTelephone("0522123456");
        r.setTypeCuisine("Marocaine");
        r.setStatut(StatutRestaurant.OUVERT);

        ResponseEntity<Restaurant> response = restTemplate.postForEntity("/api/restaurants", r, Restaurant.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        createdRestaurantId = response.getBody().getId();
        assertThat(createdRestaurantId).isPositive();
        System.out.println("✅ Restaurant créé, id=" + createdRestaurantId);
    }

    // ========================= TEST 2 : Vérification de l'existence =========================
    @Test
    @Order(2)
    void test02_RestaurantExists() {
        ResponseEntity<Boolean> response = restTemplate.getForEntity("/api/restaurants/" + createdRestaurantId + "/exists", Boolean.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isTrue();
        System.out.println("✅ Existence vérifiée");
    }

    // ========================= TEST 3 : Récupération par ID =========================
    @Test
    @Order(3)
    void test03_GetRestaurantById() {
        ResponseEntity<Restaurant> response = restTemplate.getForEntity("/api/restaurants/" + createdRestaurantId, Restaurant.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(createdRestaurantId);
        System.out.println("✅ Récupération par ID OK");
    }

    // ========================= TEST 4 : Ajout d'un plat =========================
    @Test
    @Order(4)
    void test04_AddPlat() {
        Plat p = new Plat();
        p.setNom("Pizza");
        p.setDescription("Délicieuse pizza");
        p.setPrix(12.5);

        ResponseEntity<Plat> response = restTemplate.postForEntity(
                "/api/restaurants/" + createdRestaurantId + "/plats", p, Plat.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        createdPlatId = response.getBody().getId();
        System.out.println("✅ Plat ajouté, id=" + createdPlatId);
    }

    // ========================= TEST 5 : Récupération des plats =========================
    @Test
    @Order(5)
    void test05_GetPlats() {
        ResponseEntity<Plat[]> response = restTemplate.getForEntity("/api/restaurants/" + createdRestaurantId + "/plats", Plat[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        System.out.println("✅ Liste des plats récupérée");
    }

    // ========================= TEST 6 : Mise à jour du restaurant =========================
    @Test
    @Order(6)
    void test06_UpdateRestaurant() {
        Restaurant updated = new Restaurant();
        updated.setNom("Nouveau Nom");
        updated.setAdresse("Nouvelle Adresse");
        updated.setTelephone("0699999999");
        updated.setTypeCuisine("Italienne");
        updated.setStatut(StatutRestaurant.FERME);

        ResponseEntity<Restaurant> response = restTemplate.exchange(
                "/api/restaurants/" + createdRestaurantId,
                HttpMethod.PUT,
                new HttpEntity<>(updated),
                Restaurant.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNom()).isEqualTo("Nouveau Nom");
        System.out.println("✅ Mise à jour OK");
    }

    // ========================= TEST 7 : Suppression du plat =========================
    @Test
    @Order(7)
    void test07_DeletePlat() {

        // supprimer le plat via API
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/restaurants/plats/" + createdPlatId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // vérifier qu'il n'existe plus
        ResponseEntity<Plat[]> platsResponse = restTemplate.getForEntity(
                "/api/restaurants/" + createdRestaurantId + "/plats",
                Plat[].class
        );

        assertThat(platsResponse.getBody()).doesNotContainNull();
        assertThat(platsResponse.getBody().length).isEqualTo(0);

        System.out.println("✅ Plat supprimé");
    }
    // ========================= TEST 8 : Liste de tous les restaurants =========================
    @Test
    @Order(8)
    void test08_GetAllRestaurants() {
        ResponseEntity<Restaurant[]> response = restTemplate.getForEntity("/api/restaurants", Restaurant[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        System.out.println("✅ Liste de tous les restaurants OK");
    }

    // ========================= TEST 9 : Suppression du restaurant =========================
    @Test
    @Order(9)
    void test09_DeleteRestaurant() {
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/restaurants/" + createdRestaurantId,
                HttpMethod.DELETE,
                null,
                Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Restaurant> getResponse = restTemplate.getForEntity("/api/restaurants/" + createdRestaurantId, Restaurant.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        System.out.println("✅ Restaurant supprimé");
    }
}