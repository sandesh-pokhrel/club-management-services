package com.fitness.clientservice.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "client_question")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ClientQuestion extends AuditEntity {

    @Id
    private String clientUsername;
    private Boolean questionnaireInitiated;
    private String questionnaireSerial;
}
