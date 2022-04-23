package com.fitness.clientservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    @Size(max = 30, message = "First name cannot be more than 30 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "Username can contain only alphabets, digits and underscores")
    private String username;

    @NotBlank(message = "Firstname cannot be blank")
    @Size(max = 30, message = "First name cannot be more than 30 characters long")
    @Pattern(regexp = "^[a-zA-Z]*$", message = "First name can contain only alphabets")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Lastname cannot be blank")
    @Size(max = 30, message = "Last name cannot be more than 30 characters long")
    @Pattern(regexp = "^[a-zA-Z]*$", message = "Last name can contain only alphabets")
    @Column(nullable = false)
    private String lastName;

    private char gender;
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email address format is invalid")
    @Column(nullable = false, unique = true)
    private String email;

    @Past(message = "Future date provided for dob field")
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dob;

    @NotBlank(message = "Cell number cannot be blank")
    @Pattern(regexp="[\\d]{7,20}", message = "Mobile number should contain only numbers of 7-20 characters long")
    @Column(nullable = false, unique = true)
    private String cellPhone;
    private String homePhone;
    private String workPhone;
    private String emergencyContact;
    private String emergencyContactPhone;
    private String address;
    private String city;
    private String province;
    private Integer postalCode;
    private Boolean isProspect;
    private String dependentRelation;
    private String dependentUsername;

    @OneToMany(mappedBy = "client")
    @JsonManagedReference
    private List<ClientNote> notes;

    @OneToMany(mappedBy = "client")
    @JsonManagedReference
    private List<ClientAssessment> clientAssessments;

    @OneToOne(mappedBy = "client", fetch = FetchType.EAGER)
    @JsonManagedReference
    private ClientGoal clientGoal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "club_id", referencedColumnName = "id", updatable = false,
            foreignKey = @ForeignKey(name = "fk_client_club_id"))
    private Club club;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Client client = (Client) o;
        return username.equals(client.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username);
    }
}
