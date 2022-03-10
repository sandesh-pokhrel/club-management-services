package com.fitness.clientservice.controller;

import com.fitness.clientservice.model.ClientExtraInfo;
import com.fitness.clientservice.model.ClientQuestionnaire;
import com.fitness.clientservice.model.Questionnaire;
import com.fitness.clientservice.request.ClientQuestionnaireRequest;
import com.fitness.clientservice.request.mapper.ClientQuestionnaireRequestMapper;
import com.fitness.clientservice.service.ClientExtraInfoService;
import com.fitness.clientservice.service.QuestionnaireService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/questions")
@AllArgsConstructor
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;
    private final ClientExtraInfoService clientExtraInfoService;
    private final ClientQuestionnaireRequestMapper clientQuestionnaireRequestMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Questionnaire> getAllEnabled() {
        return questionnaireService.getAllEnabledQuestions();
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<Questionnaire> getAll() {
        return questionnaireService.getAllQuestions();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Questionnaire save(@RequestBody Questionnaire questionnaire) {
        return questionnaireService.saveQuestion(questionnaire);
    }

    @PostMapping("/answers/{serial}")
    @ResponseStatus(HttpStatus.CREATED)
    public void onQuestionnaireSubmitted(@PathVariable String serial,
                                         @RequestBody List<ClientQuestionnaireRequest> clientQuestionnaireRequests) {
        ClientExtraInfo clientExtraInfo = this.clientExtraInfoService.getClientExtraInfo(serial);
        List<ClientQuestionnaire> clientQuestionnaires = new ArrayList<>();
        clientQuestionnaireRequests.forEach(clientQuestionnaireRequest -> {
                    ClientQuestionnaire clientQuestionnaire = clientQuestionnaireRequestMapper
                            .from(clientQuestionnaireRequest, questionnaireService);
                    clientQuestionnaire.setClientUsername(clientExtraInfo.getClientUsername());
                    clientQuestionnaires.add(clientQuestionnaire);
                }
        );
        this.questionnaireService.saveAllClientQuestionnaire(clientQuestionnaires);
        this.clientExtraInfoService.nullifyQuestionnaireSerialForUsername(clientExtraInfo.getClientUsername());
    }

    @GetMapping("/client-answers/{username}")
    @ResponseStatus(HttpStatus.OK)
    public List<ClientQuestionnaire> getAllClientAnswers(@PathVariable String username) {
        return questionnaireService.getAllClientAnsers(username);
    }
}
