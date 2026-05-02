package com.example.restaurantservice.service;

import com.example.restaurantservice.entity.Plat;
import com.example.restaurantservice.entity.Restaurant;
import com.example.restaurantservice.repository.PlatRepository;
import com.example.restaurantservice.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private PlatRepository platRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    
    // RESTAURANT TESTS
    

    @Test
    void testAjouterRestaurant() {
        Restaurant r = new Restaurant();
        r.setNom("Pizza House");

        when(restaurantRepository.save(r)).thenReturn(r);

        Restaurant result = restaurantService.ajouterRestaurant(r);

        assertNotNull(result);
        assertEquals("Pizza House", result.getNom());
        verify(restaurantRepository, times(1)).save(r);
    }

    @Test
    void testGetAllRestaurants() {
        List<Restaurant> list = List.of(
                new Restaurant(1L, "R1", "A1", "0600", "desc", 0.0, 0.0, true),
                new Restaurant(2L, "R2", "A2", "0601", "desc", 0.0, 0.0, true)
        );

        when(restaurantRepository.findAll()).thenReturn(list);

        List<Restaurant> result = restaurantService.getAllRestaurants();

        assertEquals(2, result.size());
        verify(restaurantRepository, times(1)).findAll();
    }

    @Test
    void testGetRestaurantByIdExists() {
        Restaurant r = new Restaurant(1L, "Pizza", "Casa", "0600", "desc", 0.0, 0.0, true);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(r));

        Optional<Restaurant> result = restaurantService.getRestaurantById(1L);

        assertTrue(result.isPresent());
        assertEquals("Pizza", result.get().getNom());
    }

    @Test
    void testGetRestaurantByIdNotFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Restaurant> result = restaurantService.getRestaurantById(1L);

        assertTrue(result.isEmpty());
    }

    
    // CAS LIMITE RESTAURANT
    

    @Test
    void testAjouterRestaurantNull() {
        when(restaurantRepository.save(null))
                .thenThrow(new IllegalArgumentException("Restaurant cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> {
            restaurantService.ajouterRestaurant(null);
        });
    }

    
    // PLATS TESTS
    

    @Test
    void testAjouterPlat() {
        Plat plat = new Plat(1L, "Pizza", "desc", 50.0, null);

        when(platRepository.save(plat)).thenReturn(plat);

        Plat result = restaurantService.ajouterPlat(plat);

        assertNotNull(result);
        assertEquals("Pizza", result.getNom());
        verify(platRepository, times(1)).save(plat);
    }

    @Test
    void testGetPlatsByRestaurant() {
        List<Plat> plats = List.of(
                new Plat(1L, "Pizza", "desc", 50.0, null),
                new Plat(2L, "Burger", "desc", 40.0, null)
        );

        when(platRepository.findByRestaurantId(1L)).thenReturn(plats);

        List<Plat> result = restaurantService.getPlatsByRestaurant(1L);

        assertEquals(2, result.size());
        verify(platRepository, times(1)).findByRestaurantId(1L);
    }

    
    // CAS LIMITE PLAT
   

    @Test
    void testGetPlatsByRestaurantEmpty() {
        when(platRepository.findByRestaurantId(99L)).thenReturn(List.of());

        List<Plat> result = restaurantService.getPlatsByRestaurant(99L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testAjouterPlatNull() {
        when(platRepository.save(null))
                .thenThrow(new IllegalArgumentException("Plat cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> {
            restaurantService.ajouterPlat(null);
        });
    }
}