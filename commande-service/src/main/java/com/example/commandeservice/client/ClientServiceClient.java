package com.example.commandeservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "client-service", url = "http://localhost:8089")
public interface ClientServiceClient {

    @GetMapping("/api/clients/{id}/exists")
    boolean existsById(@PathVariable Long id);
}