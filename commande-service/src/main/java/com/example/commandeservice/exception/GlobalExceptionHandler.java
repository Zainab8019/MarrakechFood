package com.example.commandeservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        
        String message = ex.getMessage();
        
        // Client invalide -> 400 Bad Request
        if (message != null && message.contains("Client invalide")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        
        // Restaurant invalide -> 400 Bad Request
        if (message != null && message.contains("Restaurant invalide")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        
        // Items obligatoires -> 400 Bad Request
        if (message != null && message.contains("Items obligatoires")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        
        // Commande non trouvée -> 404 Not Found
        if (message != null && message.contains("Commande non trouvée")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        
        // Commande ne peut pas être validée -> 400 Bad Request
        if (message != null && message.contains("ne peut pas être validée")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        
        // Commande doit être validée avant d'assigner un livreur -> 400 Bad Request
        if (message != null && message.contains("doit être validée avant")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        
        // Commande n'est pas en cours de livraison -> 400 Bad Request
        if (message != null && message.contains("pas en cours de livraison")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        
        // Par défaut -> 500 Internal Server Error
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}