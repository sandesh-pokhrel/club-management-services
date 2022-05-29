package com.fitness.clientservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "client_trainer_note")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ClientNote extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String note;
    private Date createdDate;
    private String trainerUsername;

    @ManyToOne
    @JoinColumn(name = "client_username", referencedColumnName = "username",
            foreignKey = @ForeignKey(name = "fk_client_trainer_note_clusername"))
    @JsonBackReference
    private Client client;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientNote that = (ClientNote) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
