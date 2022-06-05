package com.fitness.clientservice.service;

import com.fitness.clientservice.model.ClientQuestionAnswer;
import com.fitness.clientservice.model.Questionnaire;
import com.fitness.clientservice.repository.ClientQuestionAnswerRepository;
import com.fitness.clientservice.repository.QuestionnaireRepository;
import com.fitness.sharedapp.exception.BadRequestException;
import com.fitness.sharedapp.service.GenericService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class QuestionnaireService extends GenericService {

    private final QuestionnaireRepository questionnaireRepository;
    private final ClientQuestionAnswerRepository clientQuestionAnswerRepository;

    public Questionnaire getById(Integer id) {
        return this.questionnaireRepository.findById(id).orElse(null);
    }

    public List<Questionnaire> getAllQuestions() {
        return this.questionnaireRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder"));
    }

    public List<Questionnaire> getAllEnabledQuestions() {
        return this.questionnaireRepository.findAllByEnabled(true, Sort.by(Sort.Direction.ASC, "sortOrder"));
    }

    public List<Questionnaire> getAllQuestionsByClub(Integer clubId) {
        return this.questionnaireRepository.findAllByClubId(clubId, Sort.by(Sort.Direction.ASC, "sortOrder"));
    }

    public List<Questionnaire> getAllEnabledQuestionsByClub(Integer clubId) {
        return this.questionnaireRepository.findAllByEnabledAndClubId(true, clubId, Sort.by(Sort.Direction.ASC, "sortOrder"));
    }

    public Questionnaire getQuestionsById(Integer id) {
        return this.questionnaireRepository.findById(id).orElse(null);
    }

    public void saveAllClientQuestionnaire(List<ClientQuestionAnswer> clientQuestionAnswers) {
        this.clientQuestionAnswerRepository.saveAllAndFlush(clientQuestionAnswers);
    }

    public Questionnaire saveQuestion(Questionnaire questionnaire) {
        if (Objects.isNull(questionnaire.getSortOrder())) {
            questionnaire.setSortOrder(getMaxSortValue()+1);
        } else {
            List<Questionnaire> questionnaires = this.questionnaireRepository.findAllBySortOrder(questionnaire.getSortOrder());
            boolean notContains = questionnaires.stream().noneMatch(q -> Objects.equals(q.getId(), questionnaire.getId()));
            if ((Objects.isNull(questionnaire.getId()) && questionnaires.size() > 0)
            || (Objects.nonNull(questionnaire.getId()) && questionnaires.size() > 0 && notContains)) {
                List<Questionnaire> questionnaireList = this.questionnaireRepository.findAllBySortOrderGreaterThanEqual(questionnaire.getSortOrder());
                questionnaireList.forEach( q -> q.setSortOrder(q.getSortOrder() + 1));
                this.questionnaireRepository.saveAll(questionnaireList);
            }
        }
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

    public Integer getMaxSortValue() {
        return this.questionnaireRepository.findMaxSortValue();
    }
}
