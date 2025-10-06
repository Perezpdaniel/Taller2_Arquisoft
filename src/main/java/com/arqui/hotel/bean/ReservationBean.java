package com.arqui.hotel.bean;

import java.time.LocalDate;
import java.util.List;
import java.io.Serializable;

import com.arqui.hotel.Client;
import com.arqui.hotel.Reservation;
import com.arqui.hotel.ReservationState;
import com.arqui.hotel.service.ClientService;
import com.arqui.hotel.service.ReservationService;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Managed Bean for the management of reservations in the web interface.
 * Handles the interaction between the JSF view and the business services.
 */
@Named
@ViewScoped

public class ReservationBean implements Serializable {
    private static final long serialVersionUID = 1L;
    @Inject
    private ReservationService reservationService;

    @Inject
    private ClientService clientService;

    // Properties of the bean
    private Reservation reservation;
    private Reservation reservationSelected;
    private List<Reservation> reservations;
    private List<Client> clients;
    private Long clientIdSelected;
    private String searchTerm;
    private boolean editingMode;

    /**
     * Constructor that initializes the reservation empty.
     */
    public ReservationBean() {
        reservation = new Reservation();
        reservation.setStartDate(LocalDate.now().plusDays(1)); // Minimum date: tomorrow
        reservation.setEndDate(LocalDate.now().plusDays(2)); // Default date: tomorrow
        editingMode = false;
    }

    /**
     * Initializes the lists when the page is loaded.
     */
    public void initialize() {
        if (reservations == null) {
            loadReservations();
        }
        if (clients == null) {
            loadClients();
        }
    }

    /**
     * Loads all reservations from the service.
     */
    public void loadReservations() {
        try {
            reservations = reservationService.findAll();
        } catch (Exception e) {
            showErrorMessage("Error loading reservations: " + e.getMessage());
        }
    }

    /**
     * Loads all clients for the selector.
     */
    public void loadClients() {
        try {
            clients = clientService.findAll();
        } catch (Exception e) {
            showErrorMessage("Error loading clients: " + e.getMessage());
        }
    }

    /**
     * Searches for reservations by search term.
     */
    public void searchReservations() {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                reservations = reservationService.findAll();
            } else {
                // Search by room number or client name
                try {
                    Integer roomNumber = Integer.valueOf(searchTerm.trim());
                    reservations = reservationService.findByRoomNumber(roomNumber);
                } catch (NumberFormatException e) {
                    // If not a number, search by client name
                    List<Client> clientsFound = clientService.findByName(searchTerm);
                    reservations.clear();
                    for (Client client : clientsFound) {
                        reservations.addAll(reservationService.findByClient(client.getId()));
                    }
                }
            }
        } catch (Exception e) {
            showErrorMessage("Error searching reservations: " + e.getMessage());
        }
    }

    /**
     * Clears the search and reloads all reservations.
     */
    public void clearSearch() {
        searchTerm = null;
        loadReservations();
    }

    /**
     * Prepares the form to create a new reservation.
     */
    public void newReservation() {
        reservation = new Reservation();
        reservation.setStartDate(LocalDate.now().plusDays(1));
        reservation.setEndDate(LocalDate.now().plusDays(2));
        clientIdSelected = null;
        editingMode = false;
    }

    /**
     * Prepares the form to edit an existing reservation.
     */
    public void editReservation() {
        if (reservationSelected != null) {
            reservation = reservationSelected;
            clientIdSelected = reservation.getClient().getId();
            editingMode = true;
        }
    }

    /**
     * Saves a reservation (create or update).
     */
    public void saveReservation() {
        try {
            // Set the selected client
            if (clientIdSelected != null) {
                Client client = clientService.findById(clientIdSelected)
                    .orElseThrow(() -> new IllegalArgumentException("Client not found"));
                reservation.setClient(client);
            }

            if (editingMode) {
                reservationService.updateReservation(reservation);
                showSuccessMessage("Reservation updated successfully");
            } else {
                reservationService.createReservation(reservation);
                showSuccessMessage("Reservation created successfully");
            }
            
            // Clear and reload
            newReservation();
            loadReservations();
            
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        } catch (Exception e) {
            showErrorMessage("Error saving reservation: " + e.getMessage());
        }
    }

    /**
     * Cancels a selected reservation.
     */
    public void cancelReservation() {
        if (reservationSelected != null) {
            try {
                reservationService.cancelReservation(reservationSelected.getId());
                showSuccessMessage("Reservation canceled successfully");
                loadReservations();
            } catch (Exception e) {
                showErrorMessage("Error canceling reservation: " + e.getMessage());
            }
        }
    }

    /**
     * Deletes a selected reservation.
     */
    public void deleteReservation() {
        if (reservationSelected != null) {
            try {
                reservationService.deleteReservation(reservationSelected.getId());
                showSuccessMessage("Reservation deleted successfully");
                loadReservations();
            } catch (Exception e) {
                showErrorMessage("Error deleting reservation: " + e.getMessage());
            }
        }
    }

    /**
     * Cancels the current operation.
     */
    public void cancel() {
        newReservation();
    }

    /**
     * Checks if a room is available in the selected dates.
     */
    public boolean isRoomAvailable() {
        if (reservation.getRoomNumber() == null || 
            reservation.getStartDate() == null || 
            reservation.getEndDate() == null) {
            return true;
        }
        
        try {
            if (editingMode) {
                return reservationService.isRoomAvailable(
                    reservation.getRoomNumber(), 
                    reservation.getStartDate(), 
                    reservation.getEndDate());
            } else {
                return reservationService.isRoomAvailable(
                    reservation.getRoomNumber(), 
                    reservation.getStartDate(), 
                    reservation.getEndDate());
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Calculates the duration in days of the reservation.
     */
    public long calculateDurationDays() {
        if (reservation.getStartDate() != null && reservation.getEndDate() != null) {
            return reservation.calculateDurationDays();
        }
        return 0;
    }

    /**
     * Gets the minimum date for selection (tomorrow).
     */
    public LocalDate getMinimumDate() {
        return LocalDate.now().plusDays(1);
    }

    /**
     * Shows a success message.
     */
    private void showSuccessMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", message));
    }

    /**
     * Shows an error message.
     */
    private void showErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", message));
    }

    // Getters and Setters
    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Reservation getReservationSelected() {
        return reservationSelected;
    }

    public void setReservationSelected(Reservation reservationSelected) {
        this.reservationSelected = reservationSelected;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public Long getClientIdSelected() {
        return clientIdSelected;
    }

    public void setClientIdSelected(Long clientIdSelected) {
        this.clientIdSelected = clientIdSelected;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public boolean isEditingMode() {
        return editingMode;
    }

    public void setEditingMode(boolean editingMode) {
        this.editingMode = editingMode;
    }

    public ReservationState[] getReservationStates() {
        return ReservationState.values();
    }
}
