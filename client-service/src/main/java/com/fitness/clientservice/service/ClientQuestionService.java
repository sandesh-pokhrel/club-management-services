package com.fitness.clientservice.service;

import com.fitness.clientservice.model.ClientQuestion;
import com.fitness.clientservice.repository.ClientQuestionRepository;
import com.fitness.sharedapp.exception.AlreadyExistsException;
import com.fitness.sharedapp.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClientQuestionService {

    private final ClientQuestionRepository clientQuestionRepository;

    public void save(ClientQuestion clientQuestion) {
        this.clientQuestionRepository.save(clientQuestion);
    }

    public void nullifyQuestionnaireSerialForUsername(String username) {
        this.clientQuestionRepository.nullifySerialForUsername(username);
    }

    public ClientQuestion getClientExtraInfo(String serial) {
        return this.clientQuestionRepository.findByQuestionnaireSerial(serial)
                .orElseThrow(() -> new NotFoundException("Serial does not exist!"));
    }

    public void checkIfAlreadyQuestioned(String username) {
        if (this.clientQuestionRepository.existsByClientUsername(username)) {
            throw new AlreadyExistsException("Questionnaire already completed for the given user!");
        }
    }

    public void checkQuestionnaireSerialValidity(String serial) {
        if (!this.clientQuestionRepository.existsByQuestionnaireSerial(serial)) {
            throw new NotFoundException("Invalid serial number!");
        }
    }
}
