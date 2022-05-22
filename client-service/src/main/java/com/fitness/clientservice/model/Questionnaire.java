package com.fitness.clientservice.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "questionnaire")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Questionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String question;
    private String inputType;
    private String options;
    private Boolean enabled;
    private Integer questionId;
    private Integer max;

    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_questionnaire_service_id"))
    private Service service;

}
