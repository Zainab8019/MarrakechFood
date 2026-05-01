package com.example.clientservice.service;

import com.example.clientservice.entity.Client;
import com.example.clientservice.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public Client inscrire(Client client) {
        if (repository.findByEmail(client.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }
        return repository.save(client);
    }

    public Optional<Client> connexion(String email, String motDePasse) {
        Optional<Client> client = repository.findByEmail(email);
        if (client.isPresent() && client.get().getMotDePasse().equals(motDePasse)) {
            return client;
        }
        return Optional.empty();
    }

    public List<Client> getAllClients() {
        return repository.findAll();
    }

    public Optional<Client> getClientById(Long id) {
        return repository.findById(id);
    }
    public void deleteClient(Long id) {
        repository.deleteById(id);
    }
}