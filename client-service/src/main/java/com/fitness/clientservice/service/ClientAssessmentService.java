package com.fitness.clientservice.service;

import com.fitness.clientservice.model.Client;
import com.fitness.clientservice.model.ClientAssessment;
import com.fitness.clientservice.repository.ClientAssessmentRepository;
import com.fitness.sharedapp.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ClientAssessmentService {

    private final ClientAssessmentRepository clientAssessmentRepository;
    private final ClientService clientService;

    public ClientAssessment getAssessmentById(Integer id) {
        return this.clientAssessmentRepository.findById(id).orElse(null);
    }

    public void deleteAssessmentById(Integer id) {
        this.clientAssessmentRepository.deleteById(id);
    }

    public ClientAssessment saveClientAssessment(ClientAssessment clientAssessment,
                                                 String clientUsername,
                                                 String trainerUsername) {
        Client client = this.clientService.getClientByUsername(clientUsername);
        if (Objects.isNull(client)) throw new NotFoundException("Client not found");
        clientAssessment.setTrainerUsername(trainerUsername);
        clientAssessment.setClient(client);
        clientAssessment.setAssessmentDate(new Date());
        return this.clientAssessmentRepository.save(clientAssessment);
    }
}
