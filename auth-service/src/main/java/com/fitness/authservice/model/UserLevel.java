package com.fitness.authservice.model;

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
@Table(name = "user_level")
public class UserLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String levelName;
    private String levelSeniority;

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userLevel")
//    @JsonBackReference
//    private List<User> users;
}
