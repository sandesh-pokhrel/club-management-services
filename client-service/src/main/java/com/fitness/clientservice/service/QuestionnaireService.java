package com.fitness.clientservice.service;

import com.fitness.clientservice.model.ClientQuestionAnswer;
import com.fitness.clientservice.model.Questionnaire;
import com.fitness.clientservice.repository.ClientQuestionAnswerRepository;
import com.fitness.clientservice.repository.QuestionnaireRepository;
import com.fitness.sharedapp.exception.BadRequestException;
import com.fitness.sharedapp.service.GenericService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class QuestionnaireService extends GenericService {

    private final QuestionnaireRepository questionnaireRepository;
    private final ClientQuestionAnswerRepository clientQuestionAnswerRepository;

    public List<Questionnaire> getAllQuestions() {
        return this.questionnaireRepository.findAll();
    }

    public List<Questionnaire> getAllEnabledQuestions() {
        return this.questionnaireRepository.findAllByEnabled(true);
    }

    public Questionnaire getQuestionsById(Integer id) {
        return this.questionnaireRepository.findById(id).orElse(null);
    }

    public void saveAllClientQuestionnaire(List<ClientQuestionAnswer> clientQuestionAnswers) {
        this.clientQuestionAnswerRepository.saveAllAndFlush(clientQuestionAnswers);
    }

    public Questionnaire saveQuestion(Questionnaire questionnaire) {
        return this.questionnaireRepository.save(questionnaire);
    }

    public void deleteQuestion(Integer id) {
        try {
            this.questionnaireRepository.deleteById(id);
        } catch (Exception ex) {
            throw new BadRequestException("Question has associated answer. Could not delete!");
        }

    }

    public List<ClientQuestionAnswer> getAllClientAnsers(String clientUsername) {
        return this.clientQuestionAnswerRepository.findAllByClientUsername(clientUsername);
    }
}
