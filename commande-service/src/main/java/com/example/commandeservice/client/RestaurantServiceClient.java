package com.example.commandeservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "restaurant-service", url = "http://localhost:8083")
public interface RestaurantServiceClient {

    @GetMapping("/api/restaurants/{id}/exists")
    boolean existsById(@PathVariable Long id);
}