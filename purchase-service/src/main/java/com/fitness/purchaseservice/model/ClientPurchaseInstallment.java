package com.fitness.purchaseservice.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "client_purchase_installment")
public class ClientPurchaseInstallment extends AuditEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer clientPurchaseId;
    private Double amount;
    @Temporal(TemporalType.DATE)
    private Date expectedPayDate;
    @Temporal(TemporalType.DATE)
    private Date actualPayDate;
    private Boolean isPif;
    private String status;
    private String paymentMethod;

    @Transient
    private ClientPurchase clientPurchase;
}
