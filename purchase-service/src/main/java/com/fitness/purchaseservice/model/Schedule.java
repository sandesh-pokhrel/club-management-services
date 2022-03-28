package com.fitness.purchaseservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Builder
@Table(name = "schedule")
public class Schedule implements Serializable {

    public Schedule(Schedule source) {
        this.setSubject(source.getSubject());
        this.setTrainerUsername(source.getTrainerUsername());
        this.setClientUsername(source.getClientUsername());
        this.setPurchaseId(source.getPurchaseId());
        this.setStatus(source.getStatus());
        this.setStartTime(source.getStartTime());
        this.setEndTime(source.getEndTime());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String trainerUsername;
    private String clientUsername;
    private Integer purchaseId;
    private String subject;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date endTime;

    private String status;
    private Boolean isReadOnly;

    private String seriesIdentifier;
    private Integer deletedCount;

    @JsonProperty("RecurrenceRule")
    private String recurrenceRule;

    @JsonProperty("RecurrenceException")
    private String recurrenceException;

    @JsonProperty("RecurrenceID")
    private Integer recurrenceId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sub_category_id", referencedColumnName = "sub_category_id")
    @ToString.Exclude
    private PurchaseSubCategory purchaseSubCategory;
}
