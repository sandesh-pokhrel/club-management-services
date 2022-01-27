package com.fitness.authservice.model;


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
    private String email;
    private String cellPhone;
    private String level;
    private Double customRate;
    private Double groupCustomRate;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "users")
    @JsonManagedReference
    private List<Role> roles;
}
