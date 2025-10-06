package com.arqui.hotel;

/**
 * Enum that defines the possible states of a reservation.
 */

public enum ReservationState {
    
    /**
     * Reservation confirmed and active
     */
    CONFIRMED("Confirmed"),
    
    /**
     * Reservation cancelled by the client
     */
    CANCELLED("Cancelled"),
    
    /**
     * Reservation completed (check-out performed)
     */
    COMPLETED("Completed"),
    
    /**
     * Reservation in progress of check-in
     */
    IN_PROGRESS("In Progress");
    
    private final String description;
    
    ReservationState(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return description;
    }
}
