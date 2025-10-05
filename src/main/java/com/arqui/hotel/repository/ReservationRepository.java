package com.arqui.hotel.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.arqui.hotel.Reservation;
import com.arqui.hotel.ReservationState;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

/**
 * Repository for the management of Reservation entities.
 * Implements the Repository pattern to encapsulate the data access logic.
 */
@ApplicationScoped
@Transactional

public class ReservationRepository {
    @PersistenceContext(unitName = "HotelReservasDB")
    private EntityManager entityManager;

    /**
     * Saves a new reservation in the database.
     *
     * @param reservation The reservation to save
     * @return The reservation saved with its generated ID
     */
    public Reservation save(Reservation reservation) {
        entityManager.persist(reservation);
        entityManager.flush(); // Force immediate execution to obtain the ID
        return reservation;
    }

    /**
     * Updates an existing reservation.
     *
     * @param reservation The reservation to update
     * @return The updated reservation
     */
    public Reservation update(Reservation reservation) {
        return entityManager.merge(reservation);
    }

    /**
     * Deletes a reservation from the database.
     *
     * @param reservation The reservation to delete
     */
    public void delete(Reservation reservation) {
        if (!entityManager.contains(reservation)) {
            reservation = entityManager.merge(reservation);
        }
        entityManager.remove(reservation);
    }

    /**
     * Searches for a reservation by its ID.
     *
     * @param id The ID of the reservation
     * @return Optional that contains the reservation if it exists
     */
    public Optional<Reservation> findById(Long id) {
        Reservation reservation = entityManager.find(Reservation.class, id);
        return Optional.ofNullable(reservation);
    }

    /**
     * Searches for all reservations ordered by start date.
     *
     * @return List of all reservations
     */
    public List<Reservation> findAll() {
        TypedQuery<Reservation> query = entityManager.createNamedQuery("Reservation.findAll", Reservation.class);
        return query.getResultList();
    }

    /**
     * Searches for reservations by client.
     *
     * @param clientId The ID of the client
     * @return List of reservations of the client
     */
    public List<Reservation> findByClient(Long clientId) {
        TypedQuery<Reservation> query = entityManager.createNamedQuery("Reservation.findByClient", Reservation.class);
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }

    /**
     * Searches for reservations by room number.
     *
     * @param roomNumber The number of the room
     * @return List of reservations for that room
     */
    public List<Reservation> findByRoomNumber(Integer roomNumber) {
        TypedQuery<Reservation> query = entityManager.createNamedQuery("Reservation.findByRoomNumber", Reservation.class);
        query.setParameter("roomNumber", roomNumber);
        return query.getResultList();
    }

    /**
     * Searches for reservations in a date range.
     *
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return List of reservations in the range
     */
    public List<Reservation> findByDateRange(LocalDate startDate, LocalDate endDate) {
        TypedQuery<Reservation> query = entityManager.createNamedQuery("Reservation.findByDateRange", Reservation.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }

    /**
     * Searches for active reservations (confirmed and future).
     *
     * @return List of active reservations
     */
    public List<Reservation> findActiveReservations() {
        TypedQuery<Reservation> query = entityManager.createQuery(
            "SELECT r FROM Reservation r WHERE r.state = :state AND r.startDate > :today ORDER BY r.startDate",
            Reservation.class
        );
        query.setParameter("state", ReservationState.CONFIRMED);
        query.setParameter("today", LocalDate.now());
        return query.getResultList();
    }

    /**
     * Searches for active reservations (confirmed and in progress).
     *
     * @return List of active reservations
     */
    public List<Reservation> findCurrentReservations() {
        LocalDate today = LocalDate.now();
        TypedQuery<Reservation> query = entityManager.createQuery(
            "SELECT r FROM Reservation r WHERE r.state = :state AND r.startDate <= :today AND r.endDate >= :today ORDER BY r.startDate",
            Reservation.class
        );
        query.setParameter("state", ReservationState.CONFIRMED);
        query.setParameter("today", today);
        return query.getResultList();
    }

    /**
     * Checks if a room is available in a date range.
     *
     * @param roomNumber The number of the room
     * @param startDate The start date
     * @param endDate The end date
     * @param reservationIdToExclude The ID of the reservation to exclude (for updates)
     * @return true if it is available, false otherwise
     */
    public boolean isRoomAvailable(Integer roomNumber, LocalDate startDate, LocalDate endDate, Long reservationIdToExclude) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT COUNT(r) FROM Reservation r ");
        jpql.append("WHERE r.roomNumber = :roomNumber ");
        jpql.append("AND r.state = :state ");
        jpql.append("AND (r.startDate <= :endDate AND r.endDate >= :startDate) ");
        
        if (reservationIdToExclude != null) {
            jpql.append("AND r.id != :reservationIdToExclude ");
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);
        query.setParameter("roomNumber", roomNumber);
        query.setParameter("state", ReservationState.CONFIRMED);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        if (reservationIdToExclude != null) {
            query.setParameter("reservationIdToExclude", reservationIdToExclude);
        }

        return query.getSingleResult() == 0;
    }

    /**
     * Counts the total number of reservations.
     *
     * @return The total number of reservations
     */
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM Reservation r", 
            Long.class
        );
        return query.getSingleResult();
    }

    /**
     * Counts the number of reservations by state.
     *
     * @param state The state of the reservations
     * @return The number of reservations with that state
     */
    public long countByState(ReservationState state) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM Reservation r WHERE r.state = :state", 
            Long.class
        );
        query.setParameter("state", state);
        return query.getSingleResult();
    }
}
