package com.arqui.hotel.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.arqui.hotel.Client;
import com.arqui.hotel.Reservation;
import com.arqui.hotel.ReservationState;
import com.arqui.hotel.repository.ClientRepository;
import com.arqui.hotel.repository.ReservationRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Service for the management of reservations.
 * Contains the business logic related to reservations.
 */
@ApplicationScoped

public class ReservationService {
    @Inject
    private ReservationRepository reservationRepository;

    @Inject
    private ClientRepository clientRepository;

    /**
     * Creates a new reservation.
     *
     * @param reserva The reservation to create
     * @return The reservation created
     * @throws IllegalArgumentException if there are conflicts of dates or room
     */
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        // Business validations
        validateReservation(reservation);
        
        // Verify that the client exists
        Client client = clientRepository.findById(reservation.getClient().getId())
            .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        // Verify that the room is available
        if (!reservationRepository.isRoomAvailable(
                reservation.getRoomNumber(), 
                reservation.getStartDate(), 
                reservation.getEndDate(), 
                null)) {
            throw new IllegalArgumentException("The room " + reservation.getRoomNumber() + 
                " is not available in the selected dates");
        }

        // Set the client in the reservation
        reservation.setClient(client);
        
        // Set default state
        if (reservation.getState() == null) {
            reservation.setState(ReservationState.CONFIRMED);
        }

        return reservationRepository.save(reservation);
    }

    /**
     * Updates an existing reservation.
     *
     * @param reservation The reservation to update
     * @return The updated reservation
     * @throws IllegalArgumentException if there are conflicts or the reservation does not exist
     */
    @Transactional
    public Reservation updateReservation(Reservation reservation) {
        // Validate the updated reservation
        validateReservation(reservation);

        // Verify that the room is available (excluding the current reservation)
        if (!reservationRepository.isRoomAvailable(
                reservation.getRoomNumber(), 
                reservation.getStartDate(), 
                reservation.getEndDate(), 
                reservation.getId())) {
            throw new IllegalArgumentException("The room " + reservation.getRoomNumber() + 
                " is not available in the selected dates");
        }

        // Verify that the client exists
        Client client = clientRepository.findById(reservation.getClient().getId())
            .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        reservation.setClient(client);

        return reservationRepository.update(reservation);
    }

    /**
     * Cancels a reservation.
     *
     * @param id The ID of the reservation to cancel
     * @throws IllegalArgumentException if the reservation does not exist or cannot be canceled
     */
    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));

        // Solo se pueden cancelar reservas confirmadas
        if (reservation.getState() != ReservationState.CONFIRMED) {
            throw new IllegalArgumentException("Only confirmed reservations can be canceled");
        }

        reservation.setState(ReservationState.CANCELLED);
        reservationRepository.update(reservation);
    }

    /**
     * Deletes a reservation (only if it is canceled).
     *
     * @param id The ID of the reservation to delete
     * @throws IllegalArgumentException if the reservation cannot be deleted
     */
    @Transactional
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found with ID: " + id));

        // Only canceled reservations can be deleted
        if (reservation.getState() != ReservationState.CANCELLED) {
            throw new IllegalArgumentException("Only canceled reservations can be deleted");
        }

        reservationRepository.delete(reservation);
    }

    /**
     * Searches for a reservation by ID.
     *
     * @param id The ID of the reservation
     * @return Optional that contains the reservation if it exists
     */
    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    /**
     * Searches for reservations by client.
     *
     * @param clientId The ID of the client
     * @return List of reservations of the client
     */
    public List<Reservation> findByClient(Long clientId) {
        return reservationRepository.findByClient(clientId);
    }

    /**
     * Searches for reservations by room number.
     *
     * @param roomNumber The number of the room
     * @return List of reservations for that room
     */
    public List<Reservation> findByRoomNumber(Integer roomNumber) {
        return reservationRepository.findByRoomNumber(roomNumber);
    }

    /**
     * Searches for reservations in a date range.
     *
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return List of reservations in the range
     */
    public List<Reservation> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return reservationRepository.findByDateRange(startDate, endDate);
    }

    /**
     * Gets all reservations.
     *
     * @return List of all reservations
     */
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    /**
     * Gets active reservations (confirmed and future).
     *
     * @return List of active reservations
     */
    public List<Reservation> findActiveReservations() {
        return reservationRepository.findActiveReservations();
    }

    /**
     * Gets active reservations (confirmed and in progress).
     *
     * @return List of active reservations
     */
    public List<Reservation> findCurrentReservations() {
        return reservationRepository.findCurrentReservations();
    }

    /**
     * Verifies if a room is available in a date range.
     *
     * @param roomNumber The number of the room
     * @param startDate The start date
     * @param endDate The end date
     * @return true if it is available, false otherwise
     */
    public boolean isRoomAvailable(Integer roomNumber, LocalDate startDate, LocalDate endDate) {
        return reservationRepository.isRoomAvailable(roomNumber, startDate, endDate, null);
    }

    /**
     * Counts the total number of reservations.
     *
     * @return The total number of reservations
     */
    public long count() {
        return reservationRepository.count();
    }

    /**
     * Counts the number of reservations by state.
     *
     * @param state The state of the reservations
     * @return The number of reservations with that state
     */
    public long countByState(ReservationState state) {
        return reservationRepository.countByState(state);
    }

    /**
     * Validates a reservation according to the business rules.
     *
     * @param reservation The reservation to validate
     * @throws IllegalArgumentException if the reservation is not valid
     */
    private void validateReservation(Reservation reservation) {
        if (reservation.getStartDate() == null) {
            throw new IllegalArgumentException("The start date is required");
        }

        if (reservation.getEndDate() == null) {
            throw new IllegalArgumentException("The end date is required");
        }

        if (reservation.getStartDate().isAfter(reservation.getEndDate())) {
            throw new IllegalArgumentException("The start date cannot be after the end date");
        }

        if (reservation.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("The start date cannot be before today");
        }

        if (reservation.getRoomNumber() == null || reservation.getRoomNumber() <= 0) {
            throw new IllegalArgumentException("The room number must be valid");
        }

        if (reservation.getClient() == null || reservation.getClient().getId() == null) {
            throw new IllegalArgumentException("The client is required");
        }
    }
}
