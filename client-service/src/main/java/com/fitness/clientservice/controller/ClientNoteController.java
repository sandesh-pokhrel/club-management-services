package com.fitness.clientservice.controller;

import com.fitness.clientservice.feign.AuthFeignClient;
import com.fitness.clientservice.model.ClientNote;
import com.fitness.clientservice.request.ClientNoteRequest;
import com.fitness.clientservice.request.mapper.ClientNoteRequestMapper;
import com.fitness.clientservice.service.ClientNoteService;
import com.fitness.clientservice.service.ClientService;
import com.fitness.sharedapp.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
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
        if (Objects.isNull(clientNote.getClient()) || Objects.isNull(clientNote.getTrainerUsername()))
            throw new NotFoundException("Client or Trainer not found for the given request");
        return this.clientNoteService.saveNote(clientNote);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateClientNote(@PathVariable Integer id, @RequestBody Map<String, String> note) {
        this.clientNoteService.updateNote(id, note.get("note"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteClientNote(@PathVariable Integer id) {
        this.clientNoteService.deleteNote(id);
    }
}
