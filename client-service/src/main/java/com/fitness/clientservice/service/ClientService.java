package com.fitness.clientservice.service;

import com.fitness.clientservice.exception.AlreadyExistsException;
import com.fitness.clientservice.model.Client;
import com.fitness.clientservice.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public List<Client> getAllClients() {
        return this.clientRepository.findAll();
    }

    public Client getClientByUsername(String username) {
        return this.clientRepository.findById(username).orElse(Client.builder().lastName("don").build());
    }

    // TODO: Check the uniqueness of cell number and email as well
    public Client saveClient(Client client) {
        String username = client.getUsername();
        if (this.clientRepository.findById(username).isPresent())
            throw new AlreadyExistsException(String.format("User with username %s already exists!", username));
        return this.clientRepository.findById(username).orElse(null);
    }
}
