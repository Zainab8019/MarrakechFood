package com.example.clientservice.service;
import com.example.clientservice.entity.Client;
import com.example.clientservice.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
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
class ClientServiceTest {
    @Mock
    private ClientRepository repository;
    @InjectMocks
    private ClientService clientService;
    // ==================== TESTS INSCRIPTION ====================
    @Test
    void testInscrireSuccess() {
        Client client = new Client(null, "Jihane", "jihane@gmail.com", "1234", null, null);

        when(repository.findByEmail(client.getEmail())).thenReturn(Optional.empty());
        when(repository.save(any(Client.class))).thenReturn(client);

        Client result = clientService.inscrire(client);

        assertNotNull(result);
        assertEquals("jihane@gmail.com", result.getEmail());
        verify(repository).save(any(Client.class));
    }

    @Test
    void testInscrireEmailExiste() {
        Client client = new Client(null, "Jihane", "jihane@gmail.com", "1234", null, null);

        when(repository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));

        assertThrows(RuntimeException.class, () -> clientService.inscrire(client));
    }

    // ==================== TESTS CONNEXION ====================

    @Test
    void testConnexionSuccess() {
        Client client = new Client(1L, "Jihane", "jihane@gmail.com", "1234", null, null);

        when(repository.findByEmail("jihane@gmail.com")).thenReturn(Optional.of(client));

        Optional<Client> result = clientService.connexion("jihane@gmail.com", "1234");

        assertTrue(result.isPresent());
    }

    @Test
    void testConnexionWrongPassword() {
        Client client = new Client(1L, "Jihane", "jihane@gmail.com", "1234", null, null);

        when(repository.findByEmail("jihane@gmail.com")).thenReturn(Optional.of(client));

        Optional<Client> result = clientService.connexion("jihane@gmail.com", "wrong");

        assertTrue(result.isEmpty());
    }

    @Test
    void testConnexionEmailNotFound() {
        when(repository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());

        Optional<Client> result = clientService.connexion("test@gmail.com", "1234");

        assertTrue(result.isEmpty());
    }

    // ==================== AUTRES TESTS ====================

    @Test
    void testGetAllClients() {
        List<Client> clients = List.of(
                new Client(1L, "A", "a@gmail.com", "123", null, null),
                new Client(2L, "B", "b@gmail.com", "123", null, null)
        );

        when(repository.findAll()).thenReturn(clients);

        List<Client> result = clientService.getAllClients();

        assertEquals(2, result.size());
    }

    @Test
    void testGetClientByIdExists() {
        Client client = new Client(1L, "A", "a@gmail.com", "123", null, null);

        when(repository.findById(1L)).thenReturn(Optional.of(client));

        Optional<Client> result = clientService.getClientById(1L);

        assertTrue(result.isPresent());
    }

    @Test
    void testGetClientByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        Optional<Client> result = clientService.getClientById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteClient() {
        Long id = 1L;
        doNothing().when(repository).deleteById(id);

        clientService.deleteClient(id);

        verify(repository, times(1)).deleteById(id);
    }
}