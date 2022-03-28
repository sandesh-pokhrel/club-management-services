package com.fitness.clientservice.service;

import com.fitness.clientservice.model.ClientNote;
import com.fitness.clientservice.repository.ClientNoteRepository;
import com.fitness.sharedapp.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClientNoteService {

    private final ClientNoteRepository clientNoteRepository;

    public ClientNote getNoteById(Integer id) {
        return this.clientNoteRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Note does not exist!"));
    }

    public ClientNote saveNote(ClientNote clientNote) {
        return this.clientNoteRepository.save(clientNote);
    }

    public void updateNote(Integer noteId, String note) {
        this.clientNoteRepository.updateNoteById(noteId, note);
    }

    public void deleteNote(Integer id) {
        this.clientNoteRepository.delete(getNoteById(id));
    }
}
