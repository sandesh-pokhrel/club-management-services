package com.fitness.clientservice.controller;

import com.fitness.clientservice.exception.NotFoundException;
import com.fitness.clientservice.feign.AuthFeignClient;
import com.fitness.clientservice.model.ClientNote;
import com.fitness.clientservice.request.ClientNoteRequest;
import com.fitness.clientservice.request.mapper.ClientNoteRequestMapper;
import com.fitness.clientservice.service.ClientNoteService;
import com.fitness.clientservice.service.ClientService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(path = "/client-notes")
@AllArgsConstructor
public class ClientNoteController {

    private final ClientNoteService clientNoteService;
    private final ClientService clientService;
    private final AuthFeignClient authFeignClient;
    private final ClientNoteRequestMapper clientNoteRequestMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientNote saveClientNote(@RequestBody ClientNoteRequest clientNoteRequest) {
        ClientNote clientNote = this.clientNoteRequestMapper.from(clientNoteRequest, clientService, authFeignClient);
        if (Objects.isNull(clientNote.getClient()) || Objects.isNull(clientNote.getUser()))
            throw new NotFoundException("Client or Trainer not found for the given request");
        log.warn(clientNote.getClient().toString());
        log.warn(clientNote.getUser().toString());
        return this.clientNoteService.saveNote(clientNote);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteClientNote(@PathVariable Integer id) {
        this.clientNoteService.deleteNote(id);
    }
}
