package com.fitness.clientservice.controller;

import com.fitness.clientservice.model.Client;
import com.fitness.clientservice.service.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/clients")
@AllArgsConstructor

public class ClientController {

    private final ClientService clientService;

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = this.clientService.getAllClients();
        clients.forEach(client -> client.add(linkTo(methodOn(ClientController.class)
                .getClientByUsername(client.getUsername())).withSelfRel()));
        /*clients.forEach(client ->
                client.add(linkTo(ClientController.class).slash(client.getUsername()).withSelfRel()));*/
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{username}")
    @ResponseStatus(value = HttpStatus.OK)
    public Client getClientByUsername(@PathVariable String username) {
        return this.clientService.getClientByUsername(username);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Client saveClient(@RequestBody Client client) {
        return this.clientService.saveClient(client);
    }
}
