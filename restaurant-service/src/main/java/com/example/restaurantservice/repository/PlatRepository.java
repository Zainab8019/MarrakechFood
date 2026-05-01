package com.example.restaurantservice.repository;

import com.example.restaurantservice.entity.Plat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlatRepository extends JpaRepository<Plat, Long> {
    
    
    List<Plat> findByRestaurantId(Long restaurantId);
}