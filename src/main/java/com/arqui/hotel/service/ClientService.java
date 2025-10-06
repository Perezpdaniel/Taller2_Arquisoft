package com.arqui.hotel.service;

import java.util.List;
import java.util.Optional;

import com.arqui.hotel.Client;
import com.arqui.hotel.repository.ClientRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Service for the management of clients.
 * Contains the business logic related to clients.
 */
@ApplicationScoped

public class ClientService {
    @Inject
    private ClientRepository clientRepository;

    /**
     * Creates a new client.
     *
     * @param client The client to create
     * @return The client created
     * @throws IllegalArgumentException if the email already exists
     */
    @Transactional
    public Client createClient(Client client) {
        // Validate that the email does not exist
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new IllegalArgumentException("There is already a client with the email: " + client.getEmail());
        }

        return clientRepository.save(client);
    }

    /**
     * Updates an existing client.
     *
     * @param client The client to update
     * @return The updated client
     * @throws IllegalArgumentException if the email already exists in another client
     */
    @Transactional
    public Client updateClient(Client client) {
        // Validate that the email does not exist in another client
        if (clientRepository.existsByEmailExclude(client.getEmail(), client.getId())) {
            throw new IllegalArgumentException("There is already another client with the email: " + client.getEmail());
        }

        return clientRepository.update(client);
    }

    /**
     * Deletes a client.
     *
     * @param id The ID of the client to delete
     * @throws IllegalArgumentException if the client does not exist
     */
    @Transactional
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + id));

        clientRepository.delete(client);
    }

    /**
     * Searches for a client by ID.
     *
     * @param id The ID of the client
     * @return Optional that contains the client if it exists
     */
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    /**
     * Searches for a client by email.
     *
     * @param email The email of the client
     * @return Optional that contains the client if it exists
     */
    public Optional<Client> findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    /**
     * Searches for clients by name (partial search).
     *
     * @param name The name or part of the name to search
     * @return List of clients that match
     */
    public List<Client> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return clientRepository.findAll();
        }
        return clientRepository.findByName(name.trim());
    }

    /**
     * Gets all clients.
     *
     * @return List of all clients
     */
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    /**
     * Checks if an email is available.
     *
     * @param email The email to check
     * @return true if it is available, false otherwise
     */
    public boolean isEmailAvailable(String email) {
        return !clientRepository.existsByEmail(email);
    }

    /**
     * Checks if an email is available excluding a specific client.
     *
     * @param email The email to check
     * @param clientId The ID of the client to exclude
     * @return true if it is available, false otherwise
     */
    public boolean isEmailAvailableExcluding(String email, Long clientId) {
        return !clientRepository.existsByEmailExclude(email, clientId);
    }

    /**
     * Counts the total number of clients registered.
     *
     * @return The total number of clients
     */
    public long count() {
        return clientRepository.count();
    }

    /**
     * Searches for clients with basic pagination.
     *
     * @param offset The initial position
     * @param limit The maximum number of results
     * @return List of clients in the specified range
     */
    public List<Client> findAllWithPagination(int offset, int limit) {
        // Basic implementation - in a real case a query with LIMIT/OFFSET would be used
        List<Client> all = clientRepository.findAll();
        
        int start = Math.max(0, offset);
        int end = Math.min(start + limit, all.size());
        
        if (start >= all.size()) {
            return List.of();
        }
        
        return all.subList(start, end);
    }
}
