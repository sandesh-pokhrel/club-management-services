package com.fitness.clientservice.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientNoteRequest {

    private String note;
    private String clientUsername;
    private String trainerUsername;
}
