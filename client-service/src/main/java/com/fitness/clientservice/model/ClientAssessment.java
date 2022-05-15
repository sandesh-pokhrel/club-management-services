package com.fitness.clientservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "client_assessment")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ClientAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String trainerUsername;

    private Integer age;
    private Double weightPounds;
    private Double heightFeet;
    private Double heightInches;
    private Double systolicBloodPressure;
    private Double diastolicBloodPressure;
    private Double preExerciseHeartRate;
    private Double percentBodyFat;
    @Column(name = "pushups")
    private Integer pushUps;
    private Integer plank;
    private Double gripStrengthLeft;
    private Double gripStrengthRight;
    private Double straightLegRaiseLeft;
    private Double straightLegRaiseRight;
    private Double shoulderMobilityLeft;
    private Double shoulderMobilityRight;
    private Integer deepSquat;
    private Double hurdleStepLeft;
    private Double hurdleStepRight;
    private Double trunkStabilityPushups;
    private Double inlineLungeLeft;
    private Double inlineLungeRight;
    private Double rotatoryStabilityLeft;
    private Double rotatoryStabilityRight;
    private Double unipedalStanceOpenLeft;
    private Double unipedalStanceOpenRight;
    private Double unipedalStanceClosedLeft;
    private Double unipedalStanceClosedRight;

    private Double neck;
    private Double shoulder;
    private Double chest;
    private Double armAtSideLeft;
    private Double armAtSideRight;
    private Double armFlexedLeft;
    private Double armFlexedRight;
    private Double foreArmLeft;
    private Double foreArmRight;
    private Double waistOne;
    private Double waistTwo;
    private Double hips;
    private Double quadricepsLeft;
    private Double quadricepsRight;
    private Double leftCalf;
    private Double rightCalf;

    @Temporal(TemporalType.DATE)
    private Date assessmentDate;

    @ManyToOne
    @JoinColumn(name = "client_username", referencedColumnName = "username",
            foreignKey = @ForeignKey(name = "fk_client_trainer_note_clusername"))
    @JsonBackReference
    private Client client;

}
