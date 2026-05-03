package com.example.restaurantservice.controller;

import com.example.restaurantservice.entity.Plat;
import com.example.restaurantservice.entity.Restaurant;
import com.example.restaurantservice.service.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = "*")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("/test")
    public String test() {
        return "Service Restaurant fonctionne sur le port 8083 !";
    }

    @PostMapping
    public ResponseEntity<Restaurant> ajouterRestaurant(@RequestBody Restaurant restaurant) {
        Restaurant saved = restaurantService.ajouterRestaurant(restaurant);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Restaurant> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @PostMapping("/{restaurantId}/plats")
    public ResponseEntity<Plat> ajouterPlat(@PathVariable Long restaurantId, @RequestBody Plat plat) {
        // On associe le plat au restaurant
        Restaurant restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        plat.setRestaurant(restaurant);
        Plat savedPlat = restaurantService.ajouterPlat(plat);
        return ResponseEntity.ok(savedPlat);
    }

    @GetMapping("/{restaurantId}/plats")
    public List<Plat> getPlatsByRestaurant(@PathVariable Long restaurantId) {
        return restaurantService.getPlatsByRestaurant(restaurantId);
    }
    @GetMapping("/{id}/exists")
    public boolean exists(@PathVariable Long id) {
        return restaurantService.getRestaurantById(id).isPresent();
    }
}