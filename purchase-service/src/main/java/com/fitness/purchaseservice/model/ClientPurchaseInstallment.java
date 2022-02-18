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
public class ClientPurchaseInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer clientPurchaseId;
    private Double amount;
    private Date expectedPayDate;
    private Date actualPayDate;
    private Boolean isPif;
    private String status;
    private String paymentMethod;
}
