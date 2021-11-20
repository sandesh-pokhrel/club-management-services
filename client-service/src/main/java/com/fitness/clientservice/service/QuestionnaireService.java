package com.fitness.clientservice.service;

import com.fitness.clientservice.model.ClientQuestionnaire;
import com.fitness.clientservice.model.Questionnaire;
import com.fitness.clientservice.repository.ClientQuestionnaireRepository;
import com.fitness.clientservice.repository.QuestionnaireRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class QuestionnaireService extends GenericService{

    private final QuestionnaireRepository questionnaireRepository;
    private final ClientQuestionnaireRepository clientQuestionnaireRepository;

    public List<Questionnaire> getAllQuestions() {
        return this.questionnaireRepository.findAll();
    }

    public List<Questionnaire> getAllEnabledQuestions() {
        return this.questionnaireRepository.findAllByEnabled(true);
    }

    public List<ClientQuestionnaire> saveAllClientQuestionnaire(List<ClientQuestionnaire> clientQuestionnaires) {
        return this.clientQuestionnaireRepository.saveAllAndFlush(clientQuestionnaires);
    }
}
