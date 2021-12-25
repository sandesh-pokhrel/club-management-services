package com.fitness.clientservice.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientQuestionnaireRequest {

    private String answer;
    private Integer questionId;
    private String clientUsername;
}
