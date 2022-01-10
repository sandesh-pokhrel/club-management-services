package com.fitness.clientservice.service;

import com.fitness.clientservice.model.ClientQuestionnaire;
import com.fitness.clientservice.model.Questionnaire;
import com.fitness.clientservice.repository.ClientQuestionnaireRepository;
import com.fitness.clientservice.repository.QuestionnaireRepository;
import com.fitness.sharedapp.service.GenericService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class QuestionnaireService extends GenericService {

    private final QuestionnaireRepository questionnaireRepository;
    private final ClientQuestionnaireRepository clientQuestionnaireRepository;

    public List<Questionnaire> getAllQuestions() {
        return this.questionnaireRepository.findAll();
    }

    public List<Questionnaire> getAllEnabledQuestions() {
        return this.questionnaireRepository.findAllByEnabled(true);
    }

    public Questionnaire getQuestionsById(Integer id) {
        return this.questionnaireRepository.findById(id).orElse(null);
    }

    public void saveAllClientQuestionnaire(List<ClientQuestionnaire> clientQuestionnaires) {
        this.clientQuestionnaireRepository.saveAllAndFlush(clientQuestionnaires);
    }

    public List<ClientQuestionnaire> getAllClientAnsers(String clientUsername) {
        return this.clientQuestionnaireRepository.findAllByClientUsername(clientUsername);
    }
}
