package com.fitness.clientservice.service;

import com.fitness.clientservice.model.Client;
import com.fitness.clientservice.model.ClientAssessment;
import com.fitness.clientservice.repository.ClientAssessmentRepository;
import com.fitness.sharedapp.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ClientAssessmentService {

    private final ClientAssessmentRepository clientAssessmentRepository;
    private final ClientService clientService;

    public List<ClientAssessment> getAllAssessmentsForClient(String username) {
        Client client = this.clientService.getClientByUsername(username);
        if (Objects.isNull(client)) throw new NotFoundException("Client not found");
        return this.clientAssessmentRepository.findAllByClient(client);
    }

    public ClientAssessment saveClientAssessment(ClientAssessment clientAssessment, String username) {
        Client client = this.clientService.getClientByUsername(username);
        if (Objects.isNull(client)) throw new NotFoundException("Client not found");
        clientAssessment.setTrainerUsername("sandesh");
        clientAssessment.setClient(client);
        clientAssessment.setAssessmentDate(new Date());
        return this.clientAssessmentRepository.save(clientAssessment);
    }
}
