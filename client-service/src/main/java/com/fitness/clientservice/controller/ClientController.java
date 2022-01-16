package com.fitness.clientservice.controller;

import com.fitness.clientservice.model.Client;
import com.fitness.clientservice.service.ClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/clients")
@AllArgsConstructor
@Api(tags = "Client API")
@Tag(name = "Client API", description = "Contains the operation realted with the client")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Fetches all the client from database")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    public ResponseEntity<Page<Client>> getAllClients(@RequestParam Map<String, String> paramMap) {
        Page<Client> clientsPage = this.clientService.getAllClients(paramMap);
        clientsPage.getContent().forEach(client -> client.add(linkTo(methodOn(ClientController.class)
                .getClientByUsername(client.getUsername())).withSelfRel()));
        return ResponseEntity.ok(clientsPage);
    }

    @GetMapping("/usernames")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getAllClientUsernames() {
        return this.clientService.getAllClientUsernames();
    }

    @GetMapping("/username-concat-fullname/{username}")
    @ResponseStatus(HttpStatus.OK)
    public String getClientUsernameConcatFullNameByUsername(@PathVariable String username) {
        return this.clientService.getClientUsernameConcatFullNameByUsername(username);
    }

    @GetMapping("/{username}")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Fetches the client by username")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    public Client getClientByUsername(@PathVariable String username) {
        return this.clientService.getClientByUsername(username);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiOperation(value = "Saves the client after validation")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Client added"),
            @ApiResponse(code = 400, message = "Client already exists")})
    public Client saveClient(@Valid @RequestBody Client client) {
        return this.clientService.saveClient(client);
    }

    @PutMapping("/{username}")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiOperation(value = "Updates the client after validation")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Client updated"),
            @ApiResponse(code = 404, message = "Client not found")})
    public Client updateClient(@Valid @RequestBody Client client, @PathVariable String username) {
        return this.clientService.updateClient(client, username);
    }
}
