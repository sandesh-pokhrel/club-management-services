package com.fitness.authservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trainer_working_hour")
public class TrainerWorkingHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String day;
    private String startHour;
    private String endHour;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username",
            foreignKey = @ForeignKey(name = "fk_user_working_hour_username"))
    @JsonBackReference
    private User user;

}
