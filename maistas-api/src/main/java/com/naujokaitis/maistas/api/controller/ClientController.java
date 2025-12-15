package com.naujokaitis.maistas.api.controller;

import com.naujokaitis.maistas.api.exception.ResourceNotFoundException;
import com.naujokaitis.maistas.api.model.Client;
import com.naujokaitis.maistas.api.model.UserRole;
import com.naujokaitis.maistas.api.model.UserStatus;
import com.naujokaitis.maistas.api.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    // READ - Get all clients
    @GetMapping("/allClients")
    public @ResponseBody Iterable<Client> getAllClients() {
        return clientRepository.findAll();
    }

    // READ - Get single client by ID with HATEOAS links
    @GetMapping("/client/{id}")
    public EntityModel<Client> getClientById(@PathVariable UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));

        return EntityModel.of(client,
                linkTo(methodOn(ClientController.class).getClientById(id)).withSelfRel(),
                linkTo(methodOn(ClientController.class).getAllClients()).withRel("clients"));
    }

    // CREATE - Insert new client
    @PostMapping("/insertClient")
    public @ResponseBody Client createClient(@RequestBody Client client) {
        // Generate UUID if not provided
        if (client.getId() == null) {
            Client newClient = Client.create(
                    client.getUsername(),
                    client.getPasswordHash(),
                    client.getEmail(),
                    client.getPhone(),
                    client.getDefaultAddress(),
                    client.getLoyaltyPoints(),
                    client.getWalletBalance()
            );
            return clientRepository.save(newClient);
        }
        // Set default values if not provided
        if (client.getStatus() == null) {
            client.setStatus(UserStatus.ACTIVE);
        }
        if (client.getRole() == null) {
            client.setRole(UserRole.CLIENT);
        }
        if (client.getWalletBalance() == null) {
            client.setWalletBalance(BigDecimal.ZERO);
        }
        return clientRepository.save(client);
    }

    // UPDATE - Update existing client
    @PutMapping("/updateClient")
    public @ResponseBody Client updateClient(@RequestBody Client client) {
        // Verify the client exists
        if (client.getId() == null) {
            throw new IllegalArgumentException("Client ID is required for update");
        }
        clientRepository.findById(client.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", client.getId()));
        
        return clientRepository.save(client);
    }

    // UPDATE - Update client by ID with partial data
    @PutMapping("/updateClient/{id}")
    public @ResponseBody Client updateClientById(@PathVariable UUID id, @RequestBody Client clientData) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));

        // Update only non-null fields
        if (clientData.getUsername() != null) {
            existingClient.setUsername(clientData.getUsername());
        }
        if (clientData.getEmail() != null) {
            existingClient.setEmail(clientData.getEmail());
        }
        if (clientData.getPhone() != null) {
            existingClient.setPhone(clientData.getPhone());
        }
        if (clientData.getDefaultAddress() != null) {
            existingClient.setDefaultAddress(clientData.getDefaultAddress());
        }
        if (clientData.getLoyaltyPoints() != 0) {
            existingClient.setLoyaltyPoints(clientData.getLoyaltyPoints());
        }
        if (clientData.getWalletBalance() != null) {
            existingClient.setWalletBalance(clientData.getWalletBalance());
        }
        if (clientData.getStatus() != null) {
            existingClient.setStatus(clientData.getStatus());
        }

        return clientRepository.save(existingClient);
    }

    // DELETE - Delete client by ID
    @DeleteMapping("/deleteClient/{id}")
    public @ResponseBody String deleteClient(@PathVariable UUID id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client", id);
        }
        clientRepository.deleteById(id);
        return "Client with id " + id + " deleted successfully";
    }
}
