package com.fitness.clientservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "client_goal")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ClientGoal extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstGoal;
    private String secondGoal;
    private String thirdGoal;
    private String firstObstacle;
    private String secondObstacle;
    private String thirdObstacle;
    private String prescription;
    private String objection;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_username", referencedColumnName = "username")
    @JsonBackReference
    private Client client;
}
