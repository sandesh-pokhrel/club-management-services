package com.fitness.clientservice.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "client_questionnaire")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ClientQuestionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String answer;
    private Integer questionId;
    private String clientUsername;
}
