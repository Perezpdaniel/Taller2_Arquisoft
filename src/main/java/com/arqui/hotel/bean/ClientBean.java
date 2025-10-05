package com.arqui.hotel.bean;

import java.io.Serializable;
import java.util.List;

import com.arqui.hotel.Client;
import com.arqui.hotel.service.ClientService;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Managed Bean for the management of clients in the web interface.
 * Handles the interaction between the JSF view and the business services.
 */
@Named
@ViewScoped

public class ClientBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private ClientService clientService;

    // Properties of the bean
    private Client client;
    private Client clientSelected;
    private List<Client> clients;
    private String searchTerm;
    private boolean editingMode;

    /**
     * Constructor that initializes the client empty.
     */
    public ClientBean() {
        client = new Client();
        editingMode = false;
    }

    /**
     * Initializes the list of clients when the page is loaded.
     */
    public void inicializar() {
        if (clients == null) {
            loadClients();
        }
    }

    /**
     * Loads all clients from the service.
     */
    public void loadClients() {
        try {
            clients = clientService.findAll();
        } catch (Exception e) {
            showErrorMessage("Error loading clients: " + e.getMessage());
        }
    }

    /**
     * Searches for clients by name.
     */
    public void searchClients() {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                clients = clientService.findAll();
            } else {
                clients = clientService.findByName(searchTerm);
            }
        } catch (Exception e) {
            showErrorMessage("Error searching clients: " + e.getMessage());
        }
    }

    /**
     * Clears the search and reloads all clients.
     */
    public void clearSearch() {
        searchTerm = null;
        loadClients();
    }

    /**
     * Prepares the form to create a new client.
     */
    public void newClient() {
        client = new Client();
        editingMode = false;
    }

    /**
     * Prepares the form to edit an existing client.
     */
    public void editClient() {
        if (clientSelected != null) {
            client = clientSelected;
            editingMode = true;
        }
    }

    /**
     * Saves a client (create or update).
     */
    public void saveClient() {
        try {
            if (editingMode) {
                clientService.updateClient(client);
                showSuccessMessage("Client updated successfully");
            } else {
                clientService.createClient(client);
                showSuccessMessage("Client created successfully");
            }
            
            // Clear and reload
            newClient();
            loadClients();
            
        } catch (IllegalArgumentException e) {
            showErrorMessage(e.getMessage());
        } catch (Exception e) {
            showErrorMessage("Error saving client: " + e.getMessage());
        }
    }

    /**
     * Deletes a selected client.
     */
    public void deleteClient() {
        if (clientSelected != null) {
            try {
                clientService.deleteClient(clientSelected.getId());
                showSuccessMessage("Client deleted successfully");
                loadClients();
            } catch (Exception e) {
                showErrorMessage("Error deleting client: " + e.getMessage());
            }
        }
    }

    /**
     * Cancels the current operation.
     */
    public void cancel() {
        newClient();
    }

    /**
     * Checks if an email is available.
     */
    public boolean isEmailAvailable() {
        if (client.getEmail() == null || client.getEmail().trim().isEmpty()) {
            return true;
        }
        
        try {
            if (editingMode) {
                return clientService.isEmailAvailableExcluding(client.getEmail(), client.getId());
            } else {
                return clientService.isEmailAvailable(client.getEmail());
            }
        } catch (Exception e) {
            return false;
        }
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

    // Getters y Setters
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClientSelected() {
        return clientSelected;
    }

    public void setClientSelected(Client clientSelected) {
        this.clientSelected = clientSelected;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
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
}
