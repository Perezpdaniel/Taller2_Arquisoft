package com.arqui.hotel.repository;

import java.util.List;
import java.util.Optional;

import com.arqui.hotel.Client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

/**
 * Repository for the management of Client entities.
 * Implements the Repository pattern to encapsulate the data access logic.
 */
@ApplicationScoped
@Transactional

public class ClientRepository {
    
    @PersistenceContext(unitName = "HotelReservationsPU")
    private EntityManager entityManager;

    /**
     * Saves a new client in the database.
     *
     * @param cliente The client to save
     * @return The client saved with its generated ID
     */
    public Client save(Client client) {
        entityManager.persist(client);
        entityManager.flush(); // Force immediate execution to obtain the ID
        return client;
    }

    /**
     * Updates an existing client.
     *
     * @param client The client to update
     * @return The updated client
     */
    public Client update(Client client) {
        return entityManager.merge(client);
    }

    /**
     * Deletes a client from the database.
     *
     * @param client The client to delete
     */
    public void delete(Client client) {
        if (!entityManager.contains(client)) {
            client = entityManager.merge(client);
        }
        entityManager.remove(client);
    }

    /**
     * Searches for a client by its ID.
     *
     * @param id The ID of the client
     * @return Optional that contains the client if it exists
     */
    public Optional<Client> findById(Long id) {
        Client client = entityManager.find(Client.class, id);
        return Optional.ofNullable(client);
    }

    /**
     * Searches for a client by its email.
     *
     * @param email The email of the client
     * @return Optional that contains the client if it exists
     */
    public Optional<Client> findByEmail(String email) {
        TypedQuery<Client> query = entityManager.createNamedQuery("Client.findByEmail", Client.class);
        query.setParameter("email", email);
        
        try {
            Client client = query.getSingleResult();
            return Optional.of(client);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Searches for clients by name (partial search).
     *
     * @param name The name or part of the name to search
     * @return List of clients that match
     */
    public List<Client> findByName(String name) {
        TypedQuery<Client> query = entityManager.createNamedQuery("Client.findByName", Client.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }

    /**
     * Gets all clients ordered by name.
     *
     * @return List of all clients
     */
    public List<Client> findAll() {
        TypedQuery<Client> query = entityManager.createNamedQuery("Client.findAll", Client.class);
        return query.getResultList();
    }

    /**
     * Checks if there is a client with the given email.
     *
     * @param email The email to check
     * @return true if exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    /**
     * Checks if there is a client with the given email, excluding a specific ID.
     * Useful for validations during update.
     *
     * @param email The email to check
     * @param idExclude The ID of the client to exclude from the search
     * @return true if there is another client with that email, false otherwise
     */
    public boolean existsByEmailExclude(String email, Long idExclude) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM Client c WHERE c.email = :email AND c.id != :idExclude", 
            Long.class
        );
        query.setParameter("email", email);
        query.setParameter("idExclude", idExclude);
        
        return query.getSingleResult() > 0;
    }

    /**
     * Counts the total number of registered clients.
     *
     * @return The total number of clients
     */
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM Client c", 
            Long.class
        );
        return query.getSingleResult();
    }
}
