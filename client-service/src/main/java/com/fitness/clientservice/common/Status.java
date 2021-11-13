package com.fitness.clientservice.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Status implements Serializable {
    private String exMessage;
    protected String message;
    private String operation;
}
