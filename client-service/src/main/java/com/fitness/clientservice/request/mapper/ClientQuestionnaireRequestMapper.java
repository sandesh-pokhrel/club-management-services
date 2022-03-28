package com.fitness.clientservice.request.mapper;

import com.fitness.clientservice.model.ClientQuestionnaire;
import com.fitness.clientservice.request.ClientQuestionnaireRequest;
import com.fitness.clientservice.service.QuestionnaireService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ClientQuestionnaireRequestMapper {

    @Mapping(target = "questionnaire", expression = "java(questionnaireService.getQuestionsById(clientQuestionnaireRequest.getQuestionId()))")
    @Mapping(target = "id", ignore = true)
    ClientQuestionnaire from(ClientQuestionnaireRequest clientQuestionnaireRequest,
                             QuestionnaireService questionnaireService);
}
