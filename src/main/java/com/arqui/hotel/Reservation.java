package com.arqui.hotel;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Entity Reservation that represents a reservation of a room in the hotel.
 * Implements Serializable for use in JSF.
 */
@Entity
@Table(name = "reservations")
@NamedQueries({
    @NamedQuery(name = "Reservation.findAll", query = "SELECT r FROM Reservation r ORDER BY r.startDate"),
    @NamedQuery(name = "Reservation.findByClient", query = "SELECT r FROM Reservation r WHERE r.client.id = :clientId ORDER BY r.startDate"),
    @NamedQuery(name = "Reservation.findByDateRange", query = "SELECT r FROM Reservation r WHERE r.startDate >= :startDate AND r.endDate <= :endDate"),
    @NamedQuery(name = "Reservation.findByRoomNumber", query = "SELECT r FROM Reservation r WHERE r.roomNumber = :roomNumber")
})

public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "The start date is required")
    @Future(message = "The start date must be in the future")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "The end date is required")
    @Future(message = "The end date must be in the future")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull(message = "The room number is required")
    @Min(value = 1, message = "The room number must be greater than 0")
    @Column(name = "room_number", nullable = false)
    private Integer roomNumber;

    @Column(name = "observations", length = 500)
    private String observations;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private ReservationState state = ReservationState.CONFIRMED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @NotNull(message = "The client is required")
    private Client client;

    // Constructors
    public Reservation() {
    }

    public Reservation(LocalDate startDate, LocalDate endDate, Integer roomNumber, Client client) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.roomNumber = roomNumber;
        this.client = client;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public ReservationState getState() {
        return state;
    }

    public void setState(ReservationState state) {
        this.state = state;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    // Business methods
    public long calcularDuracionDias() {
        if (startDate != null && endDate != null) {
            return ChronoUnit.DAYS.between(startDate, endDate);
        }
        return 0;
    }

    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return state == ReservationState.CONFIRMED && 
               startDate != null && startDate.isAfter(today);
    }

    public boolean isCurrentReservation() {
        LocalDate today = LocalDate.now();
        return state == ReservationState.CONFIRMED && 
               startDate != null && endDate != null &&
               !startDate.isAfter(today) && !endDate.isBefore(today);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Reservation reservation = (Reservation) obj;
        return id != null && id.equals(reservation.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", roomNumber=" + roomNumber +
                ", state=" + state +
                ", client=" + (client != null ? client.getName() : "null") +
                '}';
    }
}
