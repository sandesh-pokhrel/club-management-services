package com.fitness.purchaseservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkingHour {

    private Integer id;

    private String day;
    private String startHour;
    private String endHour;
}
