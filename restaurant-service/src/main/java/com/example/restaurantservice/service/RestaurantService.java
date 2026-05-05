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

    public Optional<Restaurant> getRestaurantById(Long id) {
        return restaurantRepository.findById(id);
    }

    public Restaurant updateRestaurant(Long id, Restaurant restaurant) {

        Restaurant existing = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        existing.setNom(restaurant.getNom());
        existing.setAdresse(restaurant.getAdresse());
        existing.setTelephone(restaurant.getTelephone());
        existing.setTypeCuisine(restaurant.getTypeCuisine());
        existing.setStatut(restaurant.getStatut());

        return restaurantRepository.save(existing);
    }

    public void deleteRestaurant(Long id) {
        restaurantRepository.deleteById(id);
    }

    public Plat ajouterPlat(Plat plat) {
        return platRepository.save(plat);
    }
    

    
    public List<Plat> getPlatsByRestaurant(Long restaurantId) {
        return platRepository.findByRestaurantId(restaurantId);
    }
    public void deletePlat(Long platId) {
        Plat plat = platRepository.findById(platId)
                .orElseThrow(() -> new RuntimeException("Plat not found"));

        platRepository.delete(plat);
    }
}