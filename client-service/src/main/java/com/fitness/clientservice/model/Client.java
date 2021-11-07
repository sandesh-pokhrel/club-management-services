package com.fitness.clientservice.model;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "client")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Client extends RepresentationModel<Client> {

    @Id
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotBlank(message = "Firstname cannot be blank")
    @Size(max = 30, message = "First name cannot be more than 30 characters long")
    private String firstName;
    @NotBlank(message = "Lastname cannot be blank")
    @Size(max = 30, message = "Last name cannot be more than 30 characters long")
    private String lastName;
    private char gender;
    private String email;
    private Date dob;
    @NotBlank(message = "Cell number cannot be blank")
    @Size(max = 30, message = "Cell phone cannot be more than 20 characters long")
    private String cellPhone;
    private String homePhone;
    private String workPhone;
    private String emergencyContact;
    private String emergencyContactPhone;
    private String address;
    private String city;
    private String province;
    private String postalCode;
    private Boolean isProspect;
}
