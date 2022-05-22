package com.fitness.purchaseservice.model;

import lombok.*;
import org.codehaus.jackson.annotate.JsonBackReference;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "purchase_sub_category")
public class PurchaseSubCategory {

    @Id
    @Column(name = "sub_category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String subCategoryName;
    private Double length;
    private Double baseRate;
    private Double amount;
    private String paymentInterval;
    private Double commission;
    private Double appts;
    private Boolean paysOnCompletion;

    @Getter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    @JsonBackReference
    private PurchaseCategory purchaseCategory;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseSubCategory that = (PurchaseSubCategory) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
