package com.example.restaurantservice.service;

import com.example.restaurantservice.entity.Plat;
import com.example.restaurantservice.entity.Restaurant;
import com.example.restaurantservice.repository.PlatRepository;
import com.example.restaurantservice.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final PlatRepository platRepository;

    // Injection par constructeur (recommandé)
    public RestaurantService(RestaurantRepository restaurantRepository, 
                             PlatRepository platRepository) {
        this.restaurantRepository = restaurantRepository;
        this.platRepository = platRepository;
    }

    public Restaurant ajouterRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Plat ajouterPlat(Plat plat) {
        return platRepository.save(plat);
    }

    public List<Plat> getPlatsByRestaurant(Long restaurantId) {
        return platRepository.findByRestaurantId(restaurantId);
    }

    // Méthode bonus pour récupérer un restaurant par ID
    public Optional<Restaurant> getRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }
}