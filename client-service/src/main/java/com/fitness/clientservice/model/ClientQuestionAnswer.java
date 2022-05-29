package com.fitness.clientservice.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "client_questionnaire_answer")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ClientQuestionAnswer extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String answer;
    private String clientUsername;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "QUESTION_ID", referencedColumnName = "ID")
    private Questionnaire questionnaire;
}
