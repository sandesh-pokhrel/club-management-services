package com.fitness.purchaseservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@MappedSuperclass
public abstract class AuditEntity {

    @CreationTimestamp
    @Column(updatable = false)
    private Date createDate;

    @UpdateTimestamp
    private Date updateDate;

    @Column(updatable = false)
    private String createBy;

    private String updateBy;

    @PrePersist
    void onCreate() {
        this.createBy = SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PreUpdate
    void onUpdate() {
        this.updateBy = SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
