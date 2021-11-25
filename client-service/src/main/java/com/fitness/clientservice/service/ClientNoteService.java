package com.fitness.clientservice.service;

import com.fitness.clientservice.model.ClientNote;
import com.fitness.clientservice.repository.ClientNoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClientNoteService {

    private final ClientNoteRepository clientNoteRepository;

    public ClientNote saveNote(ClientNote clientNote) {
        return this.clientNoteRepository.save(clientNote);
    }
}
