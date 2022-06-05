package com.fitness.clientservice.controller;

import com.fitness.clientservice.model.ClientQuestion;
import com.fitness.clientservice.model.ClientQuestionAnswer;
import com.fitness.clientservice.model.Questionnaire;
import com.fitness.clientservice.request.ClientQuestionnaireRequest;
import com.fitness.clientservice.request.mapper.ClientQuestionnaireRequestMapper;
import com.fitness.clientservice.service.ClientQuestionService;
import com.fitness.clientservice.service.QuestionnaireService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/questions")
@AllArgsConstructor
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;
    private final ClientQuestionService clientQuestionService;
    private final ClientQuestionnaireRequestMapper clientQuestionnaireRequestMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Questionnaire> getAllEnabled(@RequestHeader("Club-Id") Integer clubId) {
        return questionnaireService.getAllEnabledQuestionsByClub(clubId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<Questionnaire> getAll(@RequestHeader("Club-Id") Integer clubId) {
        return questionnaireService.getAllQuestionsByClub(clubId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Questionnaire getById(@PathVariable Integer id) {
        return questionnaireService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Questionnaire save(@RequestBody Questionnaire questionnaire, @RequestHeader("Club-Id") Integer clubId) {
        questionnaire.setClubId(clubId);
        return questionnaireService.saveQuestion(questionnaire);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Integer id) {
        questionnaireService.deleteQuestion(id);
    }

    @PostMapping("/answers/{serial}")
    @ResponseStatus(HttpStatus.CREATED)
    public void onQuestionnaireSubmitted(@PathVariable String serial,
                                         @RequestBody List<ClientQuestionnaireRequest> clientQuestionnaireRequests) {
        ClientQuestion clientQuestion = this.clientQuestionService.getClientExtraInfo(serial);
        List<ClientQuestionAnswer> clientQuestionAnswers = new ArrayList<>();
        clientQuestionnaireRequests.forEach(clientQuestionnaireRequest -> {
                    ClientQuestionAnswer clientQuestionAnswer = clientQuestionnaireRequestMapper
                            .from(clientQuestionnaireRequest, questionnaireService);
                    clientQuestionAnswer.setClientUsername(clientQuestion.getClientUsername());
                    clientQuestionAnswers.add(clientQuestionAnswer);
                }
        );
        this.questionnaireService.saveAllClientQuestionnaire(clientQuestionAnswers);
        this.clientQuestionService.nullifyQuestionnaireSerialForUsername(clientQuestion.getClientUsername());
    }

    @GetMapping("/client-answers/{username}")
    @ResponseStatus(HttpStatus.OK)
    public List<ClientQuestionAnswer> getAllClientAnswers(@PathVariable String username) {
        return questionnaireService.getAllClientAnsers(username);
    }
}
