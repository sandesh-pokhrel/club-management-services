package com.fitness.purchaseservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "client_purchase")
public class ClientPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String clientUsername;
    private Integer appts;
    private String salesSource;
    private String externalSource;
    private String scheduledBy;
    private String ce1;
    private String ce2;
    private Double paymentAmount;
    private Double initialDownpayment;
    private Integer noOfPostdates;
    private String paymentInterval;
    private Date firstPostdate;
    private Integer apptScheduled;
    private Date purchaseDate;
    private String paymentMethod;
    private String paymentMethodRefNo;
    @OneToOne
    @JoinColumn(name = "sub_category_id", referencedColumnName = "sub_category_id",
        foreignKey = @ForeignKey(name = "fk_client_purchase_subcatid"))
    private PurchaseSubCategory purchaseSubCategory;
}
