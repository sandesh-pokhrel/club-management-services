package com.fitness.clientservice.service;

import com.fitness.clientservice.exception.AlreadyExistsException;
import com.fitness.clientservice.exception.NotFoundException;
import com.fitness.clientservice.model.ClientExtraInfo;
import com.fitness.clientservice.repository.ClientExtraInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClientExtraInfoService {

    private final ClientExtraInfoRepository clientExtraInfoRepository;

    public void save(ClientExtraInfo clientExtraInfo) {
        this.clientExtraInfoRepository.save(clientExtraInfo);
    }

    public void nullifyQuestionnaireSerialForUsername(String username) {
        this.clientExtraInfoRepository.nullifySerialForUsername(username);
    }

    public ClientExtraInfo getClientExtraInfo(String serial) {
        return this.clientExtraInfoRepository.findByQuestionnaireSerial(serial)
                .orElseThrow(() -> new NotFoundException("Serial does not exist!"));
    }

    public void checkIfAlreadyQuestioned(String username) {
        if (this.clientExtraInfoRepository.existsByClientUsernameAndQuestionnaireInitiated(username, true)) {
            throw new AlreadyExistsException("Questionnaire already completed for the given user!");
        }
    }

    public void checkQuestionnaireSerialValidity(String serial) {
        if (!this.clientExtraInfoRepository.existsByQuestionnaireSerial(serial)) {
            throw new NotFoundException("Invalid serial number!");
        }
    }
}
