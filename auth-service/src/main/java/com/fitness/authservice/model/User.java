package com.fitness.authservice.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auth_user")
public class User {

    @Id
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    @Column(nullable = false, unique = true)
    private String email;
    private String cellPhone;
    private Double customRate;
    private Double groupCustomRate;
    @Column(updatable = false)
    private Integer clubId;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "users")
    @JsonManagedReference
    @JsonIgnore
    private List<Role> roles;

    @ManyToOne
    @JoinColumn(name = "level", referencedColumnName = "id")
    private UserLevel userLevel;
}
