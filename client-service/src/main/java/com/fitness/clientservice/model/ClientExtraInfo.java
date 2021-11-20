package com.fitness.clientservice.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "client_extra_info")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ClientExtraInfo {
    @Id
    private String clientUsername;
    private boolean questionnaireInitiated;
    private String questionnaireSerial;
}
