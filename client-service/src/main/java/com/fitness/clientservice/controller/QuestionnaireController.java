package com.fitness.clientservice.controller;

import com.fitness.clientservice.model.ClientExtraInfo;
import com.fitness.clientservice.model.ClientQuestionnaire;
import com.fitness.clientservice.model.Questionnaire;
import com.fitness.clientservice.service.ClientExtraInfoService;
import com.fitness.clientservice.service.QuestionnaireService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
@AllArgsConstructor
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;
    private final ClientExtraInfoService clientExtraInfoService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Questionnaire> getAll() {
        return questionnaireService.getAllEnabledQuestions();
    }

    @PostMapping("/answers/{serial}")
    @ResponseStatus(HttpStatus.CREATED)
    public void onQuestionnaireSubmitted(@PathVariable String serial,
                                         @RequestBody List<ClientQuestionnaire> clientQuestionnaires) {
        ClientExtraInfo clientExtraInfo = this.clientExtraInfoService.getClientExtraInfo(serial);
        clientQuestionnaires.forEach(clientQuestionnaire ->
                clientQuestionnaire.setClientUsername(clientExtraInfo.getClientUsername()));
        this.questionnaireService.saveAllClientQuestionnaire(clientQuestionnaires);
        this.clientExtraInfoService.nullifyQuestionnaireSerialForUsername(clientExtraInfo.getClientUsername());
    }
}
